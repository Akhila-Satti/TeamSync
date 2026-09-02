package com.projects.teamsync.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.service.ApplicationService;
import com.projects.teamsync.dto.ApplicationResponse;
import com.projects.teamsync.dto.ApplicationStatusRequest;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;


@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    
    private ApplicationService applicationService;
    public ApplicationController(ApplicationService applicationService){
        this.applicationService=applicationService;
    }

    @PostMapping("/{projectId}/apply")
    public ApiResponse createApplication(@PathVariable Integer projectId) {
        return applicationService.createApplication(projectId);
    }
    @GetMapping("/{projectId}")
    public List<ApplicationResponse> viewApplications(@PathVariable Integer projectId) {
        return applicationService.viewApplications(projectId);
    }

    @PatchMapping("/{applicationId}/status")
    public ApiResponse updateApplicationStatus(@PathVariable Integer applicationId,@RequestBody ApplicationStatusRequest request){
        return applicationService.updateApplicationStatus(applicationId,request);

    }

    
    
}

