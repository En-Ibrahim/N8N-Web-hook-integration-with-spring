package com.dyarhajer.consultancy.n8nChatbot.service;

import com.dyarhajer.consultancy.n8nChatbot.dto.ChatRequest;
import com.dyarhajer.consultancy.shared.exception.N8nIntegrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ChatbotN8nServiceImpl implements N8nService {

    private final WebClient n8nWebClient;

    public ChatbotN8nServiceImpl(WebClient n8nWebClient) {
        this.n8nWebClient = n8nWebClient;
    }

    @Override
    public String sendToN8n(ChatRequest message) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("📤 Sending message to n8n - SessionId: {}, UserId: {}, Lang: {}", 
                    message.getSessionId(), message.getUserId(), message.getLang());
            log.debug("Message content: {}", message.getMessage());
            
            // Create request body
            Map<String, Object> request = new HashMap<>();
            request.put("message", message.getMessage());
            request.put("sessionId", message.getSessionId());
            request.put("userId", message.getUserId());
            request.put("lang", message.getLang());
            
            // Send request to n8n with retry logic
            String response = n8nWebClient
                    .post()
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                            .maxBackoff(Duration.ofSeconds(5))
                            .doBeforeRetry(retrySignal -> {
                                long elapsed = System.currentTimeMillis() - startTime;
                                log.warn("⚠️ Retrying n8n request... Attempt: {}, Elapsed: {}ms, Error: {}", 
                                        retrySignal.totalRetries() + 1, elapsed, retrySignal.failure().getMessage());
                            }))
                    .onErrorResume(WebClientException.class, ex -> {
                        long elapsed = System.currentTimeMillis() - startTime;
                        log.error("❌ WebClient error after {}ms: {}", elapsed, ex.getMessage());
                        return Mono.error(new N8nIntegrationException("Failed to communicate with n8n after " + elapsed + "ms", ex));
                    })
                    .defaultIfEmpty("")
                    .block(); // Block to get synchronous response
            
            long duration = System.currentTimeMillis() - startTime;
            
            // التحقق من أن الـ response ليس فارغاً
            if (response == null || response.isEmpty()) {
                log.error("⚠️ n8n returned empty response after {}ms", duration);
                return "عذراً، لم أتمكن من الحصول على رد من النظام";
            }

            log.info("✅ Received response from n8n in {}ms - Length: {} chars", duration, response.length());
            log.debug("Response content: {}", response);
            
            return response;

            
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("💥 Error sending message to n8n after {}ms: {}", duration, ex.getMessage(), ex);
            throw new N8nIntegrationException("Failed to send message to n8n workflow after " + duration + "ms", ex);
        }
    }
}
