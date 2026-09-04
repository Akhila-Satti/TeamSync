package com.projects.teamsync.dto;

import lombok.Data;

@Data
public class ProjectSkillResponse {
    private Integer projectSkillId;
    private Integer skillId;
    private String skillName;
    private Integer importance;
    
}
