package com.system.design.moviebooking.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.system.design.moviebooking.ai.agent.AgentResult;
import com.system.design.moviebooking.ai.agent.ChatAgent;
import com.system.design.moviebooking.security.AuthUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/admin/agent-test")
public class AgentTestController {

    private final ChatAgent agent;

    public AgentTestController(ChatAgent agent) {
        this.agent = agent;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AgentResult ask(@RequestBody JsonNode body, @AuthenticationPrincipal AuthUser authUser) {
        return agent.run(List.of(), body.path("message").asText(""), authUser);
    }
}