package com.system.design.moviebooking.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class AiResponse {
    private final String text;
    private final FinishReason finishReason;
    private final Integer promptTokens;
    private final Integer completionTokens;
    private final Integer totalTokens;
    private final long latencyMs;
    private final List<ToolCall> toolCalls;

}
