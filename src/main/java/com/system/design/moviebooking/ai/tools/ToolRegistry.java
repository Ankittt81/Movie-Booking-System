package com.system.design.moviebooking.ai.tools;

import com.system.design.moviebooking.ai.ToolCall;
import com.system.design.moviebooking.ai.ToolDefinition;
import com.system.design.moviebooking.security.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ToolRegistry {

    private static final Logger log = LoggerFactory.getLogger(ToolRegistry.class);

    private final Map<String, AiTool> tools = new LinkedHashMap<>();

    // Spring injects every AiTool bean here automatically
    public ToolRegistry(List<AiTool> toolBeans) {
        for (AiTool tool : toolBeans) {
            tools.put(tool.definition().getName(), tool);
        }
    }

    public List<ToolDefinition> definitions() {
        return tools.values().stream().map(AiTool::definition).toList();
    }

    public String execute(ToolCall call, AuthUser user) {
        AiTool tool = tools.get(call.getName());
        if (tool == null) {
            return ToolJson.error("Unknown tool");
        }
        try {
            return tool.execute(call.getArgumentsJson(), user);
        } catch (Exception e) {
            // the model never sees the exception text; the argument text is not logged (may hold user data)
            log.warn("Tool {} failed", call.getName(), e);
            return ToolJson.error("Tool failed");
        }
    }
}