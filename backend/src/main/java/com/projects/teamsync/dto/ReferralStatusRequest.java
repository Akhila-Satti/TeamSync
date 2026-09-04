package com.projects.teamsync.dto;

import com.projects.teamsync.enums.ReferralStatus;

import lombok.Data;

@Data
public class ReferralStatusRequest {
    private ReferralStatus referralStatus;
}
