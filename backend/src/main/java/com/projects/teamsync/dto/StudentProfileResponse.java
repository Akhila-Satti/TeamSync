package com.projects.teamsync.dto;

import java.util.List;

import com.projects.teamsync.enums.Availability;

import lombok.Data;

@Data
public class StudentProfileResponse {

    private Integer studentId;

    private String userName;

    private String bio;

    private Integer experience;

    private Availability availability;

    private List<StudentSkillResponse> skills;
}