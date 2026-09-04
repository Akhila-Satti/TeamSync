package com.projects.teamsync.dto;

import com.projects.teamsync.enums.ReferralStatus;

import lombok.Data;

@Data
public class ReferralStudentRequest {
    private ReferralStatus referralStatus;
}
