package com.projects.teamsync.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateProjectRequest {
    private String name;
    private String description;
    private String domain;
    private Integer desiredTeamSize;
    private List<ProjectSkillRequest> projectSkills;
}
