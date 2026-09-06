package com.projects.teamsync.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.CreateProjectRequest;
import com.projects.teamsync.dto.ProjectMemberResponse;
import com.projects.teamsync.dto.ProjectResponse;
import com.projects.teamsync.dto.ProjectSkillRequest;
import com.projects.teamsync.dto.SendInvitationRequest;
import com.projects.teamsync.dto.UpdateProjectRequest;
import com.projects.teamsync.dto.UpdateProjectSkill;

import com.projects.teamsync.service.InvitationService;
import com.projects.teamsync.service.ProjectMemberService;
import com.projects.teamsync.service.ProjectService;
import com.projects.teamsync.service.ProjectSkillService;



@RestController
@RequestMapping("/api/projects")
public class ProjectController {


    private InvitationService invitationService;

    private ProjectService projectService;

    private ProjectMemberService projectMemberService;

    private ProjectSkillService projectSkillService;



    public ProjectController(
            ProjectService projectService,
            ProjectMemberService projectMemberService,
            InvitationService invitationService,
            ProjectSkillService projectSkillService) {


        this.projectService =
                projectService;

        this.projectMemberService =
                projectMemberService;

        this.invitationService =
                invitationService;

        this.projectSkillService =
                projectSkillService;
    }



    // ================= CREATE PROJECT =================

    @PostMapping("/create-project")
    public ResponseEntity<ApiResponse> createProject(

            @RequestBody
            CreateProjectRequest request) {


        ApiResponse response =
                projectService
                        .createProject(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }



    // ================= GET ALL PROJECTS =================

    @GetMapping
    public ResponseEntity<List<ProjectResponse>>
            getAllProjects() {


        return ResponseEntity.ok(

                projectService
                        .getAllProjects()
        );
    }



    // ================= MY PROJECTS =================

    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjectResponse>>
            getMyProjects() {


        return ResponseEntity.ok(

                projectService
                        .getMyProjects()
        );
    }



    // ================= INVOLVED PROJECTS =================

    @GetMapping("/involved-projects")
    public ResponseEntity<List<ProjectResponse>>
            getInvolvedProjects() {


        return ResponseEntity.ok(

                projectService
                        .getInvolvedProjects()
        );
    }



    // ================= CLOSE PROJECT =================

    @PatchMapping("/{projectId}/close")
    public ResponseEntity<ApiResponse>
            closeProject(

                    @PathVariable
                    Integer projectId) {


        return ResponseEntity.ok(

                projectService
                        .closeProject(projectId)
        );
    }



    // ================= GET MEMBERS =================

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponse>>
            getProjectMembers(

                    @PathVariable
                    Integer projectId) {


        return ResponseEntity.ok(

                projectMemberService
                        .getProjectMembers(projectId)
        );
    }



    // ================= LEAVE PROJECT =================

    @PatchMapping("/{projectId}/leave")
    public ResponseEntity<ApiResponse>
            leaveProject(

                    @PathVariable
                    Integer projectId) {


        return ResponseEntity.ok(

                projectMemberService
                        .leaveProject(projectId)
        );
    }



    // ================= REMOVE MEMBER =================

    @PatchMapping(
            "/{projectId}/members/{studentId}/remove")
    public ResponseEntity<ApiResponse>
            removeMember(

                    @PathVariable
                    Integer projectId,

                    @PathVariable
                    Integer studentId) {


        return ResponseEntity.ok(

                projectMemberService
                        .removeMember(
                                projectId,
                                studentId)
        );
    }



    // ================= SEND INVITATION =================

    @PostMapping(
            "/{projectId}/invitations")
    public ResponseEntity<ApiResponse>
            sendInvitation(

                    @PathVariable
                    Integer projectId,

                    @RequestBody
                    SendInvitationRequest request) {


        ApiResponse response =
                invitationService
                        .sendInvitation(
                                projectId,
                                request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }



    // ================= UPDATE PROJECT =================

    @PatchMapping(
            "/{projectId}/update")
    public ResponseEntity<ApiResponse>
            updateProject(

                    @PathVariable
                    Integer projectId,

                    @RequestBody
                    UpdateProjectRequest request) {


        return ResponseEntity.ok(

                projectService
                        .updateProject(
                                projectId,
                                request)
        );
    }



    // ================= ADD PROJECT SKILL =================

    @PostMapping(
            "/skill/{projectId}")
    public ResponseEntity<ApiResponse>
            addProjectSkill(

                    @PathVariable
                    Integer projectId,

                    @RequestBody
                    ProjectSkillRequest request) {


        ApiResponse response =
                projectSkillService
                        .addProjectSkill(
                                projectId,
                                request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }



    // ================= UPDATE PROJECT SKILL =================

    @PatchMapping(
            "/skill/{projectSkillId}")
    public ResponseEntity<ApiResponse>
            updateProjectSkill(

                    @PathVariable
                    Integer projectSkillId,

                    @RequestBody
                    UpdateProjectSkill request) {


        return ResponseEntity.ok(

                projectSkillService
                        .updateProjectSkill(
                                projectSkillId,
                                request)
        );
    }



    // ================= DELETE PROJECT SKILL =================

    @DeleteMapping(
            "/skill/{projectSkillId}")
    public ResponseEntity<ApiResponse>
            deleteProjectSkill(

                    @PathVariable
                    Integer projectSkillId) {


        return ResponseEntity.ok(

                projectSkillService
                        .deleteProjectSkill(
                                projectSkillId)
        );
    }
    @GetMapping("/{projectId}")
public ResponseEntity<ProjectResponse> getProjectById(
        @PathVariable Integer projectId) {

    return ResponseEntity.ok(
            projectService.getProjectById(projectId)
    );
}
}