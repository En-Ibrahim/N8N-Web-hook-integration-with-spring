package com.dyarhajer.consultancy.n8nChatbot.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class N8nWebClientConfig {

    private final N8nProperties n8nProperties;

    public N8nWebClientConfig(N8nProperties n8nProperties) {
        this.n8nProperties = n8nProperties;
    }

    @Bean
    public WebClient n8nWebClient() {
        // Configure HTTP client with timeouts
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, n8nProperties.getTimeout())
                .responseTimeout(Duration.ofMillis(n8nProperties.getTimeout()))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(n8nProperties.getTimeout(), TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(n8nProperties.getTimeout(), TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(n8nProperties.getUrl())
                .defaultHeader("X-N8N-Auth-Token", n8nProperties.getAuthToken())
                .defaultHeader("Content-Type", "application/json")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
