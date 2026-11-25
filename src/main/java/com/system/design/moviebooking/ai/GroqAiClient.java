package com.system.design.moviebooking.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GroqAiClient implements AiClient {

    private static final Logger log = LoggerFactory.getLogger(GroqAiClient.class);
    private static final long MAX_WAIT_MS = 5000;

    private final RestClient restClient;
    private final AiProperties properties;

    public GroqAiClient(RestClient aiRestClient, AiProperties properties) {
        this.restClient = aiRestClient;
        this.properties = properties;
    }

    @Override
    public AiResponse chat(AiRequest request) {
        long start = System.currentTimeMillis();

        GroqResponse response = callWithRetry(buildBody(request));

        if (response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new AiException("Empty response from AI provider");
        }
        Choice choice = response.getChoices().get(0);
        GroqMessage message = choice.getMessage();

        String text = (message == null || message.getContent() == null) ? "" : message.getContent();

        List<ToolCall> toolCalls = new ArrayList<>();
        if (message != null && message.getToolCalls() != null) {
            for (GroqToolCall call : message.getToolCalls()) {
                if (call.getFunction() == null) continue;
                toolCalls.add(new ToolCall(call.getId(),
                        call.getFunction().getName(),
                        call.getFunction().getArguments()));
            }
        }

        FinishReason finishReason = mapFinishReason(choice.getFinishReason());
        if (!toolCalls.isEmpty()) {
            finishReason = FinishReason.TOOL_CALLS;   // the loop keys off this
        }

        Usage usage = response.getUsage();
        Integer promptTokens = usage == null ? null : usage.getPromptTokens();
        Integer completionTokens = usage == null ? null : usage.getCompletionTokens();
        Integer totalTokens = usage == null ? null : usage.getTotalTokens();
        long latencyMs = System.currentTimeMillis() - start;

        log.info("AI call model={} promptTokens={} completionTokens={} finish={} toolCalls={} latencyMs={}",
                properties.getModel(), promptTokens, completionTokens, finishReason, toolCalls.size(), latencyMs);

        return new AiResponse(text, finishReason, promptTokens, completionTokens, totalTokens, latencyMs, toolCalls);
    }

    // ---------- build the Groq request ----------

    private GroqRequest buildBody(AiRequest request) {
        List<GroqMessage> messages = new ArrayList<>();
        if (request.getSystemPrompt() != null && !request.getSystemPrompt().isBlank()) {
            messages.add(new GroqMessage("system", request.getSystemPrompt(), null, null));
        }
        for (ChatMessage m : request.getMessages()) {
            messages.add(toGroqMessage(m));
        }

        List<GroqTool> tools = request.getTools().isEmpty() ? null :
                request.getTools().stream()
                        .map(t -> new GroqTool("function",
                                new GroqToolFunction(t.getName(), t.getDescription(), t.getParameters())))
                        .toList();

        double temperature = request.getTemperature() != null
                ? request.getTemperature() : properties.getTemperature();
        int maxTokens = request.getMaxTokens() != null
                ? request.getMaxTokens() : properties.getMaxTokens();

        return new GroqRequest(properties.getModel(), messages, temperature, maxTokens, tools);
    }

    private GroqMessage toGroqMessage(ChatMessage m) {
        // result of a tool we ran
        if (m.getRole() == Role.TOOL) {
            return new GroqMessage("tool", m.getContent(), null, m.getToolCallId());
        }
        // the model asked for tools
        if (m.getRole() == Role.ASSISTANT && !m.getToolCalls().isEmpty()) {
            List<GroqToolCall> calls = m.getToolCalls().stream()
                    .map(c -> new GroqToolCall(c.getId(), "function",
                            new GroqFunctionCall(c.getName(), c.getArgumentsJson())))
                    .toList();
            return new GroqMessage("assistant", null, calls, null);
        }
        // plain text
        return new GroqMessage(m.getRole().name().toLowerCase(), m.getContent(), null, null);
    }

    // ---------- one HTTP call ----------

    private GroqResponse callOnce(GroqRequest body) {
        try {
            GroqResponse response = restClient.post()
                    .uri("/chat/completions")
                    .body(body)
                    .retrieve()
                    .body(GroqResponse.class);
            if (response == null) {
                throw new AiException("Empty response from AI provider");
            }
            return response;
        } catch (RestClientResponseException e) {      // Groq replied 4xx / 5xx
            int status = e.getStatusCode().value();
            log.warn("AI provider returned {}: {}", status, truncate(e.getResponseBodyAsString()));
            if (status == 429 || status >= 500) {
                throw new RetryableException("AI provider returned " + status, parseRetryAfter(e), e);
            }
            throw new AiException("AI provider returned " + status);   // 400, 401, 404...: never retry
        } catch (ResourceAccessException e) {          // timeout / connection failure
            log.warn("AI provider unreachable: {}", e.getMessage());
            throw new RetryableException("AI provider unreachable", null, e);
        } catch (RestClientException e) {              // must stay LAST: the two above are its subclasses
            if (e.getCause() instanceof IOException) {
                log.warn("AI provider timeout/IO error: {}", e.getCause().getMessage());
                throw new RetryableException("AI provider timeout", null, e);
            }
            log.warn("AI response could not be read: {}", e.getMessage());
            throw new AiException("AI response could not be read", e);
        }
    }

    private GroqResponse callWithRetry(GroqRequest body) {
        int attempt = 0;
        while (true) {
            attempt++;
            try {
                return callOnce(body);
            } catch (RetryableException e) {
                if (attempt > properties.getMaxRetries()) {
                    throw new AiException("AI provider failed after " + attempt + " attempts", e);
                }
                long wait = e.getRetryAfterMs() != null
                        ? Math.min(e.getRetryAfterMs(), MAX_WAIT_MS)
                        : properties.getBackoffMs() * (1L << (attempt - 1));   // 500, 1000, 2000...
                log.warn("AI call attempt {} failed, retrying in {} ms", attempt, wait);
                sleep(wait);
            }
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new AiException("AI call interrupted", ie);
        }
    }

    private Long parseRetryAfter(RestClientResponseException e) {
        String value = e.getResponseHeaders() == null ? null : e.getResponseHeaders().getFirst("Retry-After");
        if (value == null) return null;
        try {
            return (long) (Double.parseDouble(value) * 1000);   // header is in seconds
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private FinishReason mapFinishReason(String value) {
        if ("stop".equals(value)) return FinishReason.COMPLETE;
        if ("length".equals(value)) return FinishReason.TRUNCATED;
        if ("tool_calls".equals(value)) return FinishReason.TOOL_CALLS;
        return FinishReason.OTHER;
    }

    private String truncate(String s) {
        if (s == null) return "";
        return s.length() > 300 ? s.substring(0, 300) + "..." : s;
    }

    // ---------- Groq JSON shapes (private to this class) ----------

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)      // tools = null is not sent
    static class GroqRequest {
        private final String model;
        private final List<GroqMessage> messages;
        private final double temperature;
        @JsonProperty("max_tokens")
        private final int maxTokens;
        private final List<GroqTool> tools;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)      // null tool_calls / tool_call_id are not sent
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GroqMessage {
        private String role;
        @JsonInclude(JsonInclude.Include.ALWAYS)    // an assistant tool-call message must carry "content": null
        private String content;
        @JsonProperty("tool_calls")
        private List<GroqToolCall> toolCalls;
        @JsonProperty("tool_call_id")
        private String toolCallId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GroqToolCall {
        private String id;
        private String type;
        private GroqFunctionCall function;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GroqFunctionCall {
        private String name;
        private String arguments;      // JSON text, parsed later by the tool code
    }

    @Getter
    @AllArgsConstructor
    static class GroqTool {
        private final String type;
        private final GroqToolFunction function;
    }

    @Getter
    @AllArgsConstructor
    static class GroqToolFunction {
        private final String name;
        private final String description;
        private final Map<String, Object> parameters;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GroqResponse {
        private List<Choice> choices;
        private Usage usage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Choice {
        private GroqMessage message;
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }

    static class RetryableException extends AiException {
        private final Long retryAfterMs;   // from Groq's Retry-After header, may be null

        RetryableException(String message, Long retryAfterMs, Throwable cause) {
            super(message, cause);
            this.retryAfterMs = retryAfterMs;
        }

        Long getRetryAfterMs() {
            return retryAfterMs;
        }
    }
}