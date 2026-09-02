package com.projects.teamsync.dto;

import java.time.LocalDateTime;

import com.projects.teamsync.entity.Student;
import com.projects.teamsync.enums.ProjectStatus;

import lombok.Data;

@Data
public class ProjectResponse {
    private Integer projectId;
    private String name;
    private String description;
    private String domain;
    private Integer desiredTeamSize;
    private ProjectStatus status;
    private LocalDateTime createdAt;
    private Integer creatorId;
    
}
