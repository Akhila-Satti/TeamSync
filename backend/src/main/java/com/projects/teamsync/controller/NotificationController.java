package com.projects.teamsync.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.NotificationResponse;
import com.projects.teamsync.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private NotificationService
            notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService =
                notificationService;
    }

    @GetMapping
    public List<NotificationResponse>
            getMyNotifications() {

        return notificationService
                .getMyNotifications();
    }

    @PatchMapping("/{notificationId}/read")
    public ApiResponse markNotificationAsRead(
            @PathVariable Integer notificationId) {

        return notificationService
                .markNotificationAsRead(
                        notificationId);
    }
}