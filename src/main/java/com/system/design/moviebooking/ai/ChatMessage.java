package com.system.design.moviebooking.ai;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatMessage {
    private final Role role;
    private final String content;
    private final List<ToolCall> toolCalls;   // never null; empty unless the assistant asks for tools
    private final String toolCallId;          // null unless role is TOOL

    // existing constructor: old code (SmartSearchService) keeps compiling
    public ChatMessage(Role role, String content) {
        this(role, content, List.of(), null);
    }

    // the one that really sets everything; private so callers use the factories
    private ChatMessage(Role role, String content, List<ToolCall> toolCalls, String toolCallId) {
        this.role = role;
        this.content = content;
        this.toolCalls = (toolCalls == null) ? List.of() : List.copyOf(toolCalls);
        this.toolCallId = toolCallId;
    }

    // a normal question from the person
    public static ChatMessage user(String text) {
        return new ChatMessage(Role.USER, text, List.of(), null);
    }

    // a normal text answer from the model
    public static ChatMessage assistant(String text) {
        return new ChatMessage(Role.ASSISTANT, text, List.of(), null);
    }

    // the model asked for tools: no text, only the calls
    public static ChatMessage assistantToolCalls(List<ToolCall> calls) {
        return new ChatMessage(Role.ASSISTANT, null, calls, null);
    }

    // your answer to one tool call: the text you got from running the tool, tied to the call's id
    public static ChatMessage toolResult(String toolCallId, String content) {
        return new ChatMessage(Role.TOOL, content, List.of(), toolCallId);
    }
}