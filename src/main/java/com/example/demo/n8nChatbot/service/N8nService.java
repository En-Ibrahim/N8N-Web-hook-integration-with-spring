package com.dyarhajer.consultancy.n8nChatbot.service;

import com.dyarhajer.consultancy.n8nChatbot.dto.ChatRequest;

public interface N8nService {
    
    /**
     * Send message to n8n workflow
     * 
     * @param message the message to send
     * @return response from n8n
     */
    String sendToN8n(ChatRequest message);
}
