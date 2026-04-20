package com.dyarhajer.consultancy.n8nChatbot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {


    private String sessionId;
    @NotBlank(message = "Message cannot be empty")
    @Size(max = 5000, message = "Message cannot exceed 5000 characters")
    private String message;
    private Long userId;
    private String lang;

}