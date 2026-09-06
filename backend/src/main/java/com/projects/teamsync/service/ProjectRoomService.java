package com.projects.teamsync.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ProjectRoomResponse;
import com.projects.teamsync.entity.ProjectMember;
import com.projects.teamsync.entity.ProjectRoom;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.enums.ProjectMemberStatus;
import com.projects.teamsync.exception.UnauthorizedException;
import com.projects.teamsync.repository.ProjectMemberRepository;
import com.projects.teamsync.repository.ProjectRoomRepository;
import com.projects.teamsync.repository.StudentRepository;

@Service
public class ProjectRoomService {

    private ProjectRoomRepository projectRoomRepository;
    private ProjectMemberRepository projectMemberRepository;
    private StudentRepository studentRepository;

    public ProjectRoomService(
            ProjectRoomRepository projectRoomRepository,
            ProjectMemberRepository projectMemberRepository,
            StudentRepository studentRepository) {

        this.projectRoomRepository = projectRoomRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.studentRepository = studentRepository;
    }

    private Student getAuthenticatedStudent() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {

            throw new UnauthorizedException(
                    "Authenticated user not found");
        }

        return student;
    }

 public List<ProjectRoomResponse> getMyProjectRooms() {

    Student student =
            getAuthenticatedStudent();

    List<ProjectMember> memberships =
            projectMemberRepository
                    .findByStudentAndStatus(
                            student,
                            ProjectMemberStatus.ACTIVE);

    List<ProjectRoomResponse> responses =
            new ArrayList<>();

    for (ProjectMember membership : memberships) {

        ProjectRoom room =
                projectRoomRepository
                        .findByProject(
                                membership.getProject())
                        .orElse(null);

        if (room == null) {
            continue;
        }

        ProjectRoomResponse response =
                new ProjectRoomResponse();

        response.setRoomId(room.getId());

        response.setProjectId(
                room.getProject().getId());

        response.setProjectName(
                room.getProject().getName());

        responses.add(response);
    }

    return responses;
}}