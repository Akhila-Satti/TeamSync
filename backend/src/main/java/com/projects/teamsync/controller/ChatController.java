package com.projects.teamsync.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.MessageResponse;
import com.projects.teamsync.dto.SendMessageRequest;
import com.projects.teamsync.service.MessageService;

@RestController
@RequestMapping("/api/rooms")
public class ChatController {

    private MessageService messageService;

    public ChatController(
            MessageService messageService) {

        this.messageService =
                messageService;
    }

    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/{roomId}/chat")
    public MessageResponse sendMessage(
            @DestinationVariable Integer roomId,
            @Payload SendMessageRequest request,
            Principal principal) {

        if (principal == null) {

            throw new RuntimeException(
                    "Unauthorized user");
        }

        String email =
                principal.getName();

        return messageService
                .sendMessage(
                        roomId,
                        email,
                        request);
    }

    @GetMapping("/{roomId}/messages")
    public List<MessageResponse> getMessages(
            @PathVariable Integer roomId) {

        return messageService
                .getMessages(roomId);
    }
}