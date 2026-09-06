package com.projects.teamsync.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.enums.ApplicationStatus;

import lombok.Data;

@Data
public class ApplicationResponse {

    private Integer applicationId;

    private Integer applicantId;

    private String userName;

    private Integer projectId;

    private String projectName;

    private String projectDescription;

    private ApplicationStatus status;

    private LocalDateTime date;

    private List<Skill> skills;
}