package com.projects.teamsync.dto;

import lombok.Data;

@Data
public class UpdateProjectRequest {
    private String name;
    private String description;
    private String domain;
    private Integer desiredTeamSize;
}
