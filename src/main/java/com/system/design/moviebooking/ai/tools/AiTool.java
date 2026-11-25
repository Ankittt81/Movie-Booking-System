package com.system.design.moviebooking.ai.tools;

import com.system.design.moviebooking.ai.ToolDefinition;
import com.system.design.moviebooking.security.AuthUser;

public interface AiTool {
    ToolDefinition definition();                          // name, description, schema sent to the model
    String execute(String argumentsJson, AuthUser user);  // returns JSON text, never throws for bad input
}
