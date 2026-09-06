package com.projects.teamsync.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.enums.InvitationStatus;
import com.projects.teamsync.enums.ProjectStatus;

import lombok.Data;

@Data
public class InvitationResponse {
    private Integer invitationId;
    private Integer projectId;
    private String projectName;
    private Integer sentToStudentId;
    private String sentToUserName;
    private String sentBy;
    private String description;
    private List<Skill> projectSkills;
    private ProjectStatus projectStatus;
    private LocalDateTime sentAt;
    private InvitationStatus status;
}
