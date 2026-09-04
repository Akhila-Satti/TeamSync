package com.projects.teamsync.dto;

import com.projects.teamsync.enums.InvitationStatus;

import lombok.Data;

@Data
public class InvitationStatusRequest {
    private InvitationStatus status;
}
