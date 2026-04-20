package com.dyarhajer.consultancy.n8nChatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    private String message;
    private LocalDateTime timestamp;
    private ResponseStatus status;
    
    public enum ResponseStatus {
        SUCCESS,
        ERROR
    }
    
    public static ChatResponse success(String message) {
        return new ChatResponse(message, LocalDateTime.now(), ResponseStatus.SUCCESS);
    }
    
    public static ChatResponse error(String message) {
        return new ChatResponse(message, LocalDateTime.now(), ResponseStatus.ERROR);
    }
}
