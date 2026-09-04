package com.projects.teamsync.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.StudentSkillRequest;
import com.projects.teamsync.dto.UpdateStudentSkill;

import com.projects.teamsync.service.StudentSkillService;

@RestController
@RequestMapping("/api/student-skills")
public class StudentSkillController {

    private StudentSkillService
            studentSkillService;


    public StudentSkillController(
            StudentSkillService
                    studentSkillService) {

        this.studentSkillService =
                studentSkillService;
    }


    // ================= ADD SKILL =================

    @PostMapping
    public ResponseEntity<ApiResponse>
            addStudentSkills(

                    @RequestBody
                    StudentSkillRequest request) {

        ApiResponse response =
                studentSkillService
                        .addStudentSkills(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ================= UPDATE SKILL =================

    @PatchMapping("/{studentSkillId}")
    public ResponseEntity<ApiResponse>
            updateStudentSkill(

                    @PathVariable
                    Integer studentSkillId,

                    @RequestBody
                    UpdateStudentSkill request) {

        return ResponseEntity.ok(

                studentSkillService
                        .updateStudentSkill(
                                studentSkillId,
                                request)
        );
    }


    // ================= DELETE SKILL =================

    @DeleteMapping("/{studentSkillId}")
    public ResponseEntity<ApiResponse>
            deleteStudentSkill(

                    @PathVariable
                    Integer studentSkillId) {

        return ResponseEntity.ok(

                studentSkillService
                        .deleteStudentSkill(
                                studentSkillId)
        );
    }
}