package com.dyarhajer.consultancy.n8nChatbot.controller;

import com.dyarhajer.consultancy.n8nChatbot.dto.ChatRequest;
import com.dyarhajer.consultancy.n8nChatbot.dto.ChatResponse;
import com.dyarhajer.consultancy.n8nChatbot.service.N8nService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "N8N chat Client")
public class ChatRestController {

    private static final Logger log = LoggerFactory.getLogger(ChatRestController.class);
    
    private final N8nService n8nService;
    
    @Value("${security.ip-blacklist:}")
    private String ipBlacklist;

    public ChatRestController(N8nService n8nService) {
        this.n8nService = n8nService;
    }

    /**
     * Main chat endpoint - forwards requests to n8n workflow
     * POST http://localhost:8080/api/chat
     * Body: { "message": "your message" }
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request,
                                             HttpServletRequest httpRequest) {
        
        String ip = getClientIP(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        // Log request
        log.info("Chat request - IP: {}, User-Agent: {}, Message: {}", 
                 ip, userAgent, truncate(request.getMessage(), 50));
        
        // Check IP blacklist
        if (isBlacklisted(ip)) {
            log.warn("Blocked request from blacklisted IP: {}", ip);
            return ResponseEntity.status(403)
                    .body(ChatResponse.error("Access denied"));
        }
        
        // Send to n8n
        String n8nResponse = n8nService.sendToN8n(request);
        
        return ResponseEntity.ok(ChatResponse.success(n8nResponse));
    }



    /**
     * Extracts the client's IP address from the request.
     * Checks X-Forwarded-For header first (for proxy/load balancer scenarios).
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
    
    /**
     * Checks if the given IP address is in the blacklist.
     */
    private boolean isBlacklisted(String ip) {
        if (ipBlacklist == null || ipBlacklist.isEmpty()) {
            return false;
        }
        List<String> blacklistedIps = Arrays.asList(ipBlacklist.split(","));
        return blacklistedIps.contains(ip.trim());
    }
    
    /**
     * Truncates a string to the specified length and adds ellipsis.
     */
    private String truncate(String str, int length) {
        return str.length() > length ? str.substring(0, length) + "..." : str;
    }
}
