package com.projects.teamsync.controller;

import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.StudentSkillRequest;
import com.projects.teamsync.service.StudentSkillService;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/student-skills")
public class StudentSkillController {
    private StudentSkillService studentSkillService;
    public StudentSkillController(StudentSkillService studentSkillService){
        this.studentSkillService=studentSkillService;
    }
    @PostMapping
    public ApiResponse addStudentSkills(@RequestBody List<StudentSkillRequest> skills) {
       return studentSkillService.addStudentSkills(skills);
    }
    

}
