package com.projects.teamsync.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.CreateProjectRequest;
import com.projects.teamsync.dto.ProjectResponse;
import com.projects.teamsync.service.ProjectService;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("api/projects")
public class ProjectController {

    private ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService=projectService;
    }

    @PostMapping("/create-project")
    public ApiResponse createProject(@RequestBody CreateProjectRequest createProjectRequest) {
        
        return projectService.createProject(createProjectRequest);
    }

    @GetMapping
    public List<ProjectResponse> getAllProjects() {
        return projectService.getAllProjects();
    }
    @GetMapping("/my-projects")
    public List<ProjectResponse> getMyProjects(){
        return projectService.getMyProjects();
    }
    @PatchMapping("/{projectId}/close")
    public ApiResponse closeProject(@PathVariable Integer projectId){
        return projectService.closeProject(projectId);
    }
}
