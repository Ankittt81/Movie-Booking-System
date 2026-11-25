package com.system.design.moviebooking.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ToolDefinition {
    private final String name;
    private final String description;
    private final Map<String,Object> parameters;
}
