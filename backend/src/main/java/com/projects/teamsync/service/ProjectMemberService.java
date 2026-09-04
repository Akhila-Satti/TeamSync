package com.projects.teamsync.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.ProjectMemberResponse;

import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectMember;
import com.projects.teamsync.entity.Student;

import com.projects.teamsync.enums.NotificationType;
import com.projects.teamsync.enums.ProjectMemberRole;
import com.projects.teamsync.enums.ProjectMemberStatus;

import com.projects.teamsync.exception.BadRequestException;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;
import com.projects.teamsync.exception.AccessDeniedException;

import com.projects.teamsync.repository.ProjectMemberRepository;
import com.projects.teamsync.repository.ProjectRepository;
import com.projects.teamsync.repository.StudentRepository;


@Service
public class ProjectMemberService {

    private ProjectMemberRepository projectMemberRepository;
    private ProjectRepository projectRepository;
    private StudentRepository studentRepository;
    private NotificationService notificationService;


    public ProjectMemberService(
            ProjectMemberRepository projectMemberRepository,
            ProjectRepository projectRepository,
            StudentRepository studentRepository,
            NotificationService notificationService) {

        this.projectMemberRepository =
                projectMemberRepository;

        this.projectRepository =
                projectRepository;

        this.studentRepository =
                studentRepository;

        this.notificationService =
                notificationService;
    }


    // ================= GET PROJECT MEMBERS =================

    public List<ProjectMemberResponse> getProjectMembers(
            Integer projectId) {


        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"));


        List<ProjectMember> members =
                projectMemberRepository
                        .findByProjectAndStatus(
                                project,
                                ProjectMemberStatus.ACTIVE);


        List<ProjectMemberResponse> projectMembers =
                new ArrayList<>();


        for (ProjectMember member : members) {

            ProjectMemberResponse response =
                    new ProjectMemberResponse();


            response.setJoinedAt(
                    member.getJoinedAt());

            response.setRole(
                    member.getRole());

            response.setStudentId(
                    member.getStudent().getId());

            response.setUserName(
                    member.getStudent().getUserName());


            projectMembers.add(response);
        }


        return projectMembers;
    }



    // ================= LEAVE PROJECT =================

    public ApiResponse leaveProject(
            Integer projectId) {


        Student student =
                getAuthenticatedStudent();


        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"));


        ProjectMember member =
                projectMemberRepository
                        .findByProjectAndStudent(
                                project,
                                student)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "You are not a member of this project"));


        if (member.getStatus() !=
                ProjectMemberStatus.ACTIVE) {

            throw new BadRequestException(
                    "You are not an active member of this project");
        }


        if (member.getRole() ==
                ProjectMemberRole.CREATOR) {

            throw new BadRequestException(
                    "Project creator cannot leave the project");
        }


        // Update member status

        member.setStatus(
                ProjectMemberStatus.LEAVE);

        member.setLeftAt(
                LocalDateTime.now());


        projectMemberRepository.save(member);


        // Notify project creator

        notificationService.createNotification(

                project.getCreatedBy(),

                NotificationType.MEMBER_LEFT,

                student.getUserName()
                        + " has left your project "
                        + project.getName()
                        + "."
        );


        return new ApiResponse(
                true,
                "Successfully left the project");
    }



    // ================= REMOVE MEMBER =================

    public ApiResponse removeMember(
            Integer projectId,
            Integer studentId) {


        Student creator =
                getAuthenticatedStudent();


        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"));


        // Only creator can remove members

        if (!project.getCreatedBy()
                .getId()
                .equals(creator.getId())) {

           throw new AccessDeniedException(
        "You are not authorized to remove members from this project");
        }


        Student student =
                studentRepository
                        .findById(studentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found"));


        ProjectMember member =
                projectMemberRepository
                        .findByProjectAndStudent(
                                project,
                                student)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student is not a member of this project"));


        if (member.getStatus() !=
                ProjectMemberStatus.ACTIVE) {

            throw new BadRequestException(
                    "Student is not an active member of this project");
        }


        if (member.getRole() ==
                ProjectMemberRole.CREATOR) {

            throw new BadRequestException(
                    "Project creator cannot be removed");
        }


        // Update member status

        member.setStatus(
                ProjectMemberStatus.REMOVED);

        member.setLeftAt(
                LocalDateTime.now());


        projectMemberRepository.save(
                member);


        // Notify removed member

        notificationService.createNotification(

                student,

                NotificationType.MEMBER_REMOVED,

                "You have been removed from the project "
                        + project.getName()
                        + "."
        );


        return new ApiResponse(
                true,
                "Member removed successfully");
    }



    // ================= HELPER METHOD =================

    private Student getAuthenticatedStudent() {


        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();


        Student student =
                studentRepository
                        .findByEmail(email);


        if (student == null) {

            throw new UnauthorizedException(
                    "Authenticated user not found");
        }


        return student;
    }
}