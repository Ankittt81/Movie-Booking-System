package com.system.design.moviebooking.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiResponse {
    private final String text;
    private final FinishReason finishReason;
    private final Integer promptTokens;
    private final Integer completionTokens;
    private final Integer totalTokens;
    private final long latencyMs;

}
