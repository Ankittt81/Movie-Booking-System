package com.system.design.moviebooking.ai;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AiRequest {
    private final String systemPrompt;
    private final List<ChatMessage> messages;
    private final Double temperature;
    private final Integer maxTokens;
    private final List<ToolDefinition> tools;

    // no tools: delegates to the full constructor
    public AiRequest(String systemPrompt, List<ChatMessage> messages, Double temperature, Integer maxTokens) {
        this(systemPrompt, messages, temperature, maxTokens, List.of());
    }

    public AiRequest(String systemPrompt, List<ChatMessage> messages, Double temperature,
                     Integer maxTokens, List<ToolDefinition> tools) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("At least one message is required");
        }
        this.systemPrompt = systemPrompt;
        this.messages = List.copyOf(messages);
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.tools = (tools == null) ? List.of() : List.copyOf(tools);
    }

    public static AiRequest ofUser(String systemPrompt, String userText) {
        return new AiRequest(systemPrompt, List.of(ChatMessage.user(userText)), null, null);
    }

    public static AiRequest ofConversation(String systemPrompt, List<ChatMessage> history, String userText) {
        List<ChatMessage> all = new ArrayList<>(history);
        all.add(ChatMessage.user(userText));
        return new AiRequest(systemPrompt, all, null, null);
    }
}
