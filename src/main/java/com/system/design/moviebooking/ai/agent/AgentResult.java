package com.system.design.moviebooking.ai.agent;

import com.system.design.moviebooking.ai.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AgentResult {
    private final String answer;                  // the text to show the user
    private final List<ChatMessage> messages;     // everything added this turn, in order (step 4 will save these)
}