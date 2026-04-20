package com.dyarhajer.consultancy.n8nChatbot.config;

import com.dyarhajer.consultancy.n8nChatbot.config.RateLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for n8n chatbot module.
 * Registers the rate limiting interceptor for chat endpoints.
 */
@Configuration
@RequiredArgsConstructor
public class ChatbotWebConfig implements WebMvcConfigurer {
    
    private final RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/chat/**");
    }
}
