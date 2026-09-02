package com.projects.teamsync.dto;

import com.projects.teamsync.enums.ApplicationStatus;

import lombok.Data;

@Data
public class ApplicationStatusRequest {
    private ApplicationStatus applicationStatus;
    
}
