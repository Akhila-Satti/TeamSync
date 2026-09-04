package com.projects.teamsync.dto;

import java.time.LocalDateTime;

import com.projects.teamsync.enums.NotificationType;

import lombok.Data;

@Data
public class NotificationResponse {
    private Integer notificationId;
    private NotificationType type;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    
}
