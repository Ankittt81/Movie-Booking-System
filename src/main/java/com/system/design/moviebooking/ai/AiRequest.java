package com.system.design.moviebooking.ai;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AiRequest {
    private final String systemPrompt;
    private final List<ChatMessage> messages;
    private final Double temperature;
    private final Integer maxTokens;

    public AiRequest(String systemPrompt,List<ChatMessage> messages,Double temperature,Integer maxTokens){
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("At least one message is required");
        }
        this.systemPrompt=systemPrompt;
        this.messages=List.copyOf(messages);  // defensive copy: callers can't change it later
        this.temperature=temperature;
        this.maxTokens=maxTokens;
    }

    public static AiRequest ofUser(String systemPrompt,String userText){
        return new AiRequest(systemPrompt,List.of(new ChatMessage(Role.USER,userText)),null,null);
    }

    public static AiRequest ofConversation(String systemPrompt,List<ChatMessage> history,String userText){
        List<ChatMessage> all=new ArrayList<>(history);
        all.add(new ChatMessage(Role.USER,userText));
        return new AiRequest(systemPrompt, all,null,null);
    }
}
