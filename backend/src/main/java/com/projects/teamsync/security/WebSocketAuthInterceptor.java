package com.projects.teamsync.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.projects.teamsync.service.JwtService;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private JwtService jwtService;

    public WebSocketAuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message,
                StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null ||
                    !authHeader.startsWith("Bearer ")) {

                return message;
            }

            String token = authHeader.substring(7);

            if (jwtService.isTokenValid(token)) {

                String email = jwtService.extractEmail(token);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        null);

                accessor.setUser(authentication);

            }
        }

        return message;
    }
}