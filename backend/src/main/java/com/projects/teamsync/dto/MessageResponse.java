package com.projects.teamsync.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MessageResponse {
    private Integer roomId;
    private Integer studentId;
    private String userName;
    private String content;
    private LocalDateTime sentAt;

}
