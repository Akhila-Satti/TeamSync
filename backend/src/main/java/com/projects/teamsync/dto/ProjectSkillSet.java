package com.projects.teamsync.dto;

import com.projects.teamsync.entity.Skill;

import lombok.Data;

@Data
public class ProjectSkillSet {
    private Skill skill;
    private Integer importance;
}
