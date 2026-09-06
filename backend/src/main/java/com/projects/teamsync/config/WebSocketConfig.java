package com.projects.teamsync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.projects.teamsync.security.WebSocketAuthInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig
        implements WebSocketMessageBrokerConfigurer {

    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    
    public WebSocketConfig(
            WebSocketAuthInterceptor webSocketAuthInterceptor) {

        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic");

        registry.setApplicationDestinationPrefixes("/app");
    }

   @Override
public void registerStompEndpoints(
        StompEndpointRegistry registry) {

    registry.addEndpoint("/ws")
        .setAllowedOrigins("http://localhost:5173")
        .setAllowedOrigins("https://team-sync-395gkumfe-team-sync1.vercel.app")
        .setAllowedOrigins("https://team-sync-neon.vercel.app")
        .withSockJS();
}

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {

        registration.interceptors(webSocketAuthInterceptor);
    }
}