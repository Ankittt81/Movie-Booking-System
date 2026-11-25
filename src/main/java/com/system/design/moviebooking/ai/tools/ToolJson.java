package com.system.design.moviebooking.ai.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public final class ToolJson {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ToolJson() {}

    public static String error(String message) {
        return write(Map.of("error", message));
    }

    public static String write(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Tool failed\"}";
        }
    }

    // returns null when the text is not valid JSON; blank text counts as {}
    public static JsonNode parse(String json) {
        try {
            return MAPPER.readTree(json == null || json.isBlank() ? "{}" : json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}