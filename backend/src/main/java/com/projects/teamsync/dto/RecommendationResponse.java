package com.projects.teamsync.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecommendationResponse {
    private Integer studentId;
    private String userName;
    private Integer experience;
    private Double matchingScore;
    private List<SkillRecommendationResponse> matchedSkills;
    private List<SkillRecommendationResponse> missingSkills;

}
