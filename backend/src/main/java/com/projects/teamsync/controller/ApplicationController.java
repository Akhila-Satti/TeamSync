package com.projects.teamsync.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.ApplicationResponse;
import com.projects.teamsync.dto.ApplicationStatusRequest;

import com.projects.teamsync.service.ApplicationService;


@RestController
@RequestMapping("/api/applications")
public class ApplicationController {


    private ApplicationService applicationService;


    public ApplicationController(
            ApplicationService applicationService) {

        this.applicationService =
                applicationService;
    }


    // ================= CREATE APPLICATION =================

    @PostMapping("/{projectId}/apply")
    public ResponseEntity<ApiResponse>
            createApplication(

                    @PathVariable
                    Integer projectId) {


        ApiResponse response =
                applicationService
                        .createApplication(
                                projectId);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ================= VIEW APPLICATIONS =================

    @GetMapping("/{projectId}")
    public ResponseEntity<List<ApplicationResponse>>
            viewApplications(

                    @PathVariable
                    Integer projectId) {


        return ResponseEntity.ok(

                applicationService
                        .viewApplications(
                                projectId)
        );
    }


    // ================= UPDATE APPLICATION STATUS =================

    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<ApiResponse>
            updateApplicationStatus(

                    @PathVariable
                    Integer applicationId,

                    @RequestBody
                    ApplicationStatusRequest request) {


        return ResponseEntity.ok(

                applicationService
                        .updateApplicationStatus(
                                applicationId,
                                request)
        );
    }
}