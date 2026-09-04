package com.projects.teamsync.dto;

import java.time.LocalDateTime;

import com.projects.teamsync.enums.ReferralStatus;

import lombok.Data;

@Data
public class ReferralResponse {

    private Integer referralId;

    private Integer projectId;
    private String projectName;

    private Integer referredById;
    private String referredByName;

    private Integer referredStudentId;
    private String referredStudentName;

    private ReferralStatus status;

    private LocalDateTime createdAt;
}