package com.projects.teamsync.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String email;

    private String newPassword;

}