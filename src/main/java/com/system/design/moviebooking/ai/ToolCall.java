package com.system.design.moviebooking.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ToolCall {
    private final String id;
    private final String name;
    private final String argumentsJson;
}
