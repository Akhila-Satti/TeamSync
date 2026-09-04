package com.projects.teamsync.dto;

import java.time.LocalDateTime;

import com.projects.teamsync.enums.ProjectMemberRole;

import lombok.Data;

@Data
public class ProjectMemberResponse {
    private Integer studentId;
    private String userName;
    private ProjectMemberRole role;
    private LocalDateTime joinedAt;
}
