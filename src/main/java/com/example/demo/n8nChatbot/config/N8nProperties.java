package com.dyarhajer.consultancy.n8nChatbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "n8n.webhook")
public class N8nProperties {
    
    /**
     * n8n webhook URL
     */
    private String url;
    
    /**
     * Authentication token for n8n webhook
     */
    private String authToken;
    
    /**
     * Request timeout in milliseconds
     */
    private int timeout = 5000;
    
    /**
     * Maximum number of retry attempts
     */
    private int maxRetries = 3;
}
