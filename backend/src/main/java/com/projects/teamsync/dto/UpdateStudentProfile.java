package com.projects.teamsync.dto;

import com.projects.teamsync.enums.Availability;

import lombok.Data;

@Data
public class UpdateStudentProfile {
    private String bio;
    private Availability availability;
    private Integer experience;
}
