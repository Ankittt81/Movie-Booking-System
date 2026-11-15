package com.system.design.moviebooking.ai;

import lombok.Getter;

@Getter
public class ChatMessage {
    private  final Role role;
    private final String content;

    public ChatMessage(Role role, String content) {
        this.role = role;
        this.content = content;
    }
}
