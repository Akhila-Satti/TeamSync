package com.projects.teamsync.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.CreateProjectRequest;
import com.projects.teamsync.dto.ProjectResponse;
import com.projects.teamsync.dto.ProjectSkillRequest;
import com.projects.teamsync.dto.ProjectSkillResponse;
import com.projects.teamsync.dto.ProjectSkillSet;
import com.projects.teamsync.dto.UpdateProjectRequest;

import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectMember;
import com.projects.teamsync.entity.ProjectRoom;
import com.projects.teamsync.entity.ProjectSkill;
import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.entity.Student;

import com.projects.teamsync.enums.ProjectMemberRole;
import com.projects.teamsync.enums.ProjectMemberStatus;
import com.projects.teamsync.enums.ProjectStatus;

import com.projects.teamsync.exception.BadRequestException;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;
import com.projects.teamsync.exception.AccessDeniedException;

import com.projects.teamsync.repository.ProjectMemberRepository;
import com.projects.teamsync.repository.ProjectRepository;
import com.projects.teamsync.repository.ProjectRoomRepository;
import com.projects.teamsync.repository.ProjectSkillRepository;
import com.projects.teamsync.repository.SkillRepository;
import com.projects.teamsync.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProjectService {

    private ProjectRepository projectRepository;
    private StudentRepository studentRepository;
    private SkillRepository skillRepository;
    private ProjectSkillRepository projectSkillRepository;
    private ProjectMemberRepository projectMemberRepository;
    private ProjectRoomRepository projectRoomRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            StudentRepository studentRepository,
            SkillRepository skillRepository,
            ProjectSkillRepository projectSkillRepository,
            ProjectMemberRepository projectMemberRepository,
            ProjectRoomRepository projectRoomRepository) {

        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
        this.projectSkillRepository = projectSkillRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRoomRepository = projectRoomRepository;
    }


    // ================= CREATE PROJECT =================

    public ApiResponse createProject(CreateProjectRequest createProjectRequest) {

        if (createProjectRequest == null) {
            throw new BadRequestException("Invalid request");
        }

        String name = createProjectRequest.getName();
        String description = createProjectRequest.getDescription();
        String domain = createProjectRequest.getDomain();
        Integer desiredTeamSize = createProjectRequest.getDesiredTeamSize();

        if (name == null || name.isBlank() || name.strip().length() < 2) {
            throw new BadRequestException(
                    "Project name must be at least 2 characters");
        }

        if (domain == null || domain.isBlank()) {
            throw new BadRequestException("Must select a domain");
        }

        if (desiredTeamSize == null || desiredTeamSize <= 0) {
            throw new BadRequestException(
                    "Team size must be greater than 0");
        }


        Student student = getAuthenticatedStudent();


        List<ProjectSkillRequest> projectSkills =
                createProjectRequest.getProjectSkills();

        if (projectSkills == null || projectSkills.isEmpty()) {
            throw new BadRequestException(
                    "At least one skill is required");
        }


        HashSet<Integer> skillIds = new HashSet<>();

        List<ProjectSkillSet> validatedSkills =
                new ArrayList<>();


        for (ProjectSkillRequest projectSkill : projectSkills) {

            if (projectSkill.getSkillId() == null) {
                throw new BadRequestException(
                        "Skill ID is required");
            }

            if (projectSkill.getImportance() == null ||
                    projectSkill.getImportance() < 1 ||
                    projectSkill.getImportance() > 5) {

                throw new BadRequestException(
                        "Importance must be between 1 and 5");
            }


            Skill skill = skillRepository
                    .findById(projectSkill.getSkillId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Skill not found"));


            if (skillIds.add(skill.getId())) {

                ProjectSkillSet skillSet =
                        new ProjectSkillSet();

                skillSet.setSkill(skill);
                skillSet.setImportance(
                        projectSkill.getImportance());

                validatedSkills.add(skillSet);
            }
        }


        // Create Project

        Project project = new Project();

        project.setName(name.strip());
        project.setDescription(description);
        project.setDomain(domain.strip());
        project.setDesiredTeamSize(desiredTeamSize);

        project.setCreatedBy(student);

        project.setStatus(ProjectStatus.OPEN);

        project.setCreatedAt(LocalDateTime.now());


        Project savedProject =
                projectRepository.save(project);


        // Add Project Skills

        for (ProjectSkillSet skillSet : validatedSkills) {

            ProjectSkill projectSkill =
                    new ProjectSkill();

            projectSkill.setProject(savedProject);

            projectSkill.setSkill(
                    skillSet.getSkill());

            projectSkill.setImportance(
                    skillSet.getImportance());

            projectSkillRepository.save(projectSkill);
        }


        // Add Creator as Project Member

        ProjectMember projectMember =
                new ProjectMember();

        projectMember.setProject(savedProject);

        projectMember.setStudent(student);

        projectMember.setJoinedAt(
                LocalDateTime.now());

        projectMember.setRole(
                ProjectMemberRole.CREATOR);

        projectMember.setStatus(
                ProjectMemberStatus.ACTIVE);

        projectMemberRepository.save(
                projectMember);


        // Create Project Room

        ProjectRoom projectRoom =
                new ProjectRoom();

        projectRoom.setProject(savedProject);

        projectRoom.setCreatedAt(
                LocalDateTime.now());

        projectRoomRepository.save(
                projectRoom);


        return new ApiResponse(
                true,
                "Project created successfully");
    }


    // ================= GET ALL PROJECTS =================

    public List<ProjectResponse> getAllProjects() {

        List<Project> projects =
                projectRepository.findByStatus(
                        ProjectStatus.OPEN);

        List<ProjectResponse> responses =
                new ArrayList<>();


        for (Project project : projects) {

            responses.add(
                    convertToProjectResponse(project));
        }


        return responses;
    }


    // ================= GET MY PROJECTS =================

    public List<ProjectResponse> getMyProjects() {

        Student student =
                getAuthenticatedStudent();


        List<Project> projects =
                projectRepository.findByCreatedBy(
                        student);


        List<ProjectResponse> responses =
                new ArrayList<>();


        for (Project project : projects) {

            responses.add(
                    convertToProjectResponse(project));
        }


        return responses;
    }


    // ================= GET INVOLVED PROJECTS =================

    public List<ProjectResponse> getInvolvedProjects() {

        Student student =
                getAuthenticatedStudent();


        List<ProjectMember> involvedProjects =
                projectMemberRepository
                        .findByStudentAndStatus(
                                student,
                                ProjectMemberStatus.ACTIVE);


        List<ProjectResponse> responses =
                new ArrayList<>();


        for (ProjectMember projectMember :
                involvedProjects) {

            Project project =
                    projectMember.getProject();


            // Skip projects created by the user
            // because they already appear in My Projects

            if (project.getCreatedBy()
                    .getId()
                    .equals(student.getId())) {

                continue;
            }


            responses.add(
                    convertToProjectResponse(project));
        }


        return responses;
    }


    // ================= CLOSE PROJECT =================

    public ApiResponse closeProject(
            Integer projectId) {


        Student student =
                getAuthenticatedStudent();


        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"));


        if (!project.getCreatedBy()
                .getId()
                .equals(student.getId())) {

            throw new AccessDeniedException(
        "You are not authorized to close this project");
        }


        if (project.getStatus() ==
                ProjectStatus.CLOSED) {

            throw new BadRequestException(
                    "Project is already closed");
        }


        project.setStatus(
                ProjectStatus.CLOSED);

        project.setClosedAt(
                LocalDateTime.now());


        projectRepository.save(project);


        return new ApiResponse(
                true,
                "Project closed successfully");
    }


    // ================= UPDATE PROJECT =================

    public ApiResponse updateProject(
            Integer projectId,
            UpdateProjectRequest request) {


        if (request == null) {

            throw new BadRequestException(
                    "Invalid update request");
        }


        Student student =
                getAuthenticatedStudent();


        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"));


        if (!project.getCreatedBy()
                .getId()
                .equals(student.getId())) {

           throw new AccessDeniedException(
        "You are not authorized to update this project");
        }


        if (project.getStatus() ==
                ProjectStatus.CLOSED) {

            throw new BadRequestException(
                    "Closed projects cannot be updated");
        }


        String name =
                request.getName();

        String description =
                request.getDescription();

        String domain =
                request.getDomain();

        Integer desiredTeamSize =
                request.getDesiredTeamSize();


        long currentMemberCount =
                projectMemberRepository
                        .countByProjectAndStatus(
                                project,
                                ProjectMemberStatus.ACTIVE);


        if (name != null) {

            if (name.isBlank() ||
                    name.strip().length() < 2) {

                throw new BadRequestException(
                        "Project name must be at least 2 characters");
            }

            project.setName(
                    name.strip());
        }


        if (description != null) {

            project.setDescription(
                    description.strip());
        }


        if (domain != null) {

            if (domain.isBlank() ||
                    domain.strip().length() < 2) {

                throw new BadRequestException(
                        "Domain must be at least 2 characters");
            }

            project.setDomain(
                    domain.strip());
        }


        if (desiredTeamSize != null) {

            if (desiredTeamSize <= 0) {

                throw new BadRequestException(
                        "Team size must be greater than 0");
            }


            if (desiredTeamSize <
                    currentMemberCount) {

                throw new BadRequestException(
                        "Team size cannot be smaller than the current active member count");
            }


            project.setDesiredTeamSize(
                    desiredTeamSize);
        }


        projectRepository.save(project);


        return new ApiResponse(
                true,
                "Project updated successfully");
    }



    // ================= HELPER METHODS =================


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



    private ProjectResponse convertToProjectResponse(
            Project project) {


        long memberCount =
                projectMemberRepository
                        .countByProjectAndStatus(
                                project,
                                ProjectMemberStatus.ACTIVE);


        List<ProjectSkill> projectSkills =
                projectSkillRepository
                        .findByProject(project);


        List<ProjectSkillResponse>
                skillResponses =
                new ArrayList<>();


        for (ProjectSkill projectSkill :
                projectSkills) {


            ProjectSkillResponse response =
                    new ProjectSkillResponse();


            response.setProjectSkillId(
                    projectSkill.getId());

            response.setSkillId(
                    projectSkill
                            .getSkill()
                            .getId());

            response.setSkillName(
                    projectSkill
                            .getSkill()
                            .getName());

            response.setImportance(
                    projectSkill
                            .getImportance());


            skillResponses.add(response);
        }


        ProjectResponse response =
                new ProjectResponse();


        response.setProjectId(
                project.getId());

        response.setName(
                project.getName());

        response.setDescription(
                project.getDescription());

        response.setDomain(
                project.getDomain());

        response.setDesiredTeamSize(
                project.getDesiredTeamSize());

        response.setStatus(
                project.getStatus());

        response.setCreatedAt(
                project.getCreatedAt());

        response.setCreatorId(
                project.getCreatedBy()
                        .getId());

        response.setCreatorName(
                project.getCreatedBy()
                        .getUserName());

        response.setCurrentMemberCount(
                memberCount);

        response.setSkills(
                skillResponses);


        return response;
    }
}