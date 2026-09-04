package com.projects.teamsync.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String email;
    private String otp;
}
