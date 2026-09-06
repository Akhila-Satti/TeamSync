package com.projects.teamsync.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.ApplicationResponse;
import com.projects.teamsync.dto.ApplicationStatusRequest;

import com.projects.teamsync.entity.Application;
import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectMember;
import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.entity.StudentSkill;

import com.projects.teamsync.enums.ApplicationStatus;
import com.projects.teamsync.enums.NotificationType;
import com.projects.teamsync.enums.ProjectMemberRole;
import com.projects.teamsync.enums.ProjectMemberStatus;
import com.projects.teamsync.enums.ProjectStatus;

import com.projects.teamsync.exception.BadRequestException;
import com.projects.teamsync.exception.ConflictException;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;

import com.projects.teamsync.repository.ApplicationRepository;
import com.projects.teamsync.repository.InvitationRepository;
import com.projects.teamsync.repository.ProjectMemberRepository;
import com.projects.teamsync.repository.ProjectRepository;
import com.projects.teamsync.repository.StudentRepository;
import com.projects.teamsync.repository.StudentSkillRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ApplicationService {

    private ApplicationRepository applicationRepository;
    private ProjectRepository projectRepository;
    private StudentRepository studentRepository;
    private StudentSkillRepository studentSkillRepository;
    private NotificationService notificationService;
    private ProjectMemberRepository projectMemberRepository;
    private InvitationRepository invitationRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            ProjectRepository projectRepository,
            StudentRepository studentRepository,
            StudentSkillRepository studentSkillRepository,
            ProjectMemberRepository projectMemberRepository,
            NotificationService notificationService,
        InvitationRepository invitationRepository) {

        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.projectRepository = projectRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.notificationService = notificationService;
        this.invitationRepository=invitationRepository;
    }

    // ================= CREATE APPLICATION =================

    public ApiResponse createApplication(Integer projectId) {

        Student student = getAuthenticatedStudent();

        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        if (project.getStatus() == ProjectStatus.CLOSED) {

            throw new BadRequestException(
                    "Cannot apply to a closed project");
        }

        if (student.getId()
                .equals(project.getCreatedBy().getId())) {

            throw new BadRequestException(
                    "Project creator cannot apply to their own project");
        }

        boolean isApplied =
                applicationRepository
                        .existsByStudentAndProject(
                                student,
                                project);

        if (isApplied) {

            throw new ConflictException(
                    "You have already applied to this project");
        }

        boolean alreadyMember =
                projectMemberRepository
                        .existsByProjectAndStudent(
                                project,
                                student);

        if (alreadyMember) {

            throw new ConflictException(
                    "You are already a member of this project");
        }
        boolean isInvited=invitationRepository.existsByProjectAndSentTo(project, student);
        if (isInvited) {

            throw new ConflictException(
                    "You are already invited to this project");
        }

        long currentMembers =
                projectMemberRepository
                        .countByProjectAndStatus(
                                project,
                                ProjectMemberStatus.ACTIVE);

        if (currentMembers >=
                project.getDesiredTeamSize()) {

            throw new BadRequestException(
                    "Project team is already full");
        }

        Application application =
                new Application();

        application.setStudent(student);
        application.setProject(project);
        application.setStatus(
                ApplicationStatus.PENDING);
        application.setCreatedAt(
                LocalDateTime.now());

        applicationRepository.save(application);

        notificationService.createNotification(
                project.getCreatedBy(),
                NotificationType.APPLICATION_RECEIVED,
                student.getUserName()
                        + " has applied for your project "
                        + project.getName()
                        + ". Review the application to accept or reject.");

        return new ApiResponse(
                true,
                "Application created successfully");
    }

    // ================= MY APPLICATIONS =================

    public List<ApplicationResponse> getMyApplications() {

        Student student = getAuthenticatedStudent();

        List<Application> applications =
                applicationRepository.findByStudent(student);

        List<ApplicationResponse> responses =
                new ArrayList<>();

        for (Application application : applications) {

            ApplicationResponse response =
                    new ApplicationResponse();

            response.setApplicationId(
                    application.getId());

            response.setApplicantId(
                    student.getId());

            response.setUserName(
                    student.getUserName());

            // Project information
            response.setProjectId(
                    application.getProject().getId());

            response.setProjectName(
                    application.getProject().getName());

            response.setProjectDescription(
                    application.getProject().getDescription());

            response.setStatus(
                    application.getStatus());

            response.setDate(
                    application.getCreatedAt());

            // Student skills
            List<StudentSkill> studentSkills =
                    studentSkillRepository
                            .findByStudent(student);

            List<Skill> skills =
                    new ArrayList<>();

            for (StudentSkill studentSkill :
                    studentSkills) {

                skills.add(
                        studentSkill.getSkill());
            }

            response.setSkills(skills);

            responses.add(response);
        }

        return responses;
    }

    // ================= VIEW APPLICATIONS =================

    public List<ApplicationResponse> viewApplications(
            Integer projectId) {

        Student student = getAuthenticatedStudent();

        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));

        if (!student.getId()
                .equals(project.getCreatedBy().getId())) {

            throw new UnauthorizedException(
                    "You are not authorized to view applications for this project");
        }

        List<Application> allApplied =
                applicationRepository
                        .findByProject(project);

        List<ApplicationResponse> allApplications =
                new ArrayList<>();

        for (Application app : allApplied) {

            ApplicationResponse applicationResponse =
                    new ApplicationResponse();

            applicationResponse.setApplicantId(
                    app.getStudent().getId());

            applicationResponse.setApplicationId(
                    app.getId());

            applicationResponse.setDate(
                    app.getCreatedAt());

            // Project information
            applicationResponse.setProjectId(
                    app.getProject().getId());

            applicationResponse.setProjectName(
                    app.getProject().getName());

            applicationResponse.setProjectDescription(
                    app.getProject().getDescription());

            // Applicant skills
            List<StudentSkill> studentSkills =
                    studentSkillRepository
                            .findByStudent(
                                    app.getStudent());

            List<Skill> skills =
                    new ArrayList<>();

            for (StudentSkill studentSkill :
                    studentSkills) {

                Skill skill =
                        studentSkill.getSkill();

                skills.add(skill);
            }

            applicationResponse.setSkills(skills);

            applicationResponse.setStatus(
                    app.getStatus());

            applicationResponse.setUserName(
                    app.getStudent().getUserName());

            allApplications.add(
                    applicationResponse);
        }

        return allApplications;
    }

    // ================= UPDATE APPLICATION STATUS =================

    public ApiResponse updateApplicationStatus(
            Integer applicationId,
            ApplicationStatusRequest request) {

        if (request == null ||
                request.getApplicationStatus() == null) {

            throw new BadRequestException(
                    "Application status is required");
        }

        Student student = getAuthenticatedStudent();

        Application application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Application not found"));

        Project project =
                application.getProject();

        if (!student.getId()
                .equals(project.getCreatedBy().getId())) {

            throw new UnauthorizedException(
                    "You are not authorized to update this application");
        }

        if (application.getStatus()
                != ApplicationStatus.PENDING) {

            throw new BadRequestException(
                    "Application status cannot be changed");
        }

        ApplicationStatus status =
                request.getApplicationStatus();

        if (status != ApplicationStatus.ACCEPTED
                &&
                status != ApplicationStatus.REJECTED) {

            throw new BadRequestException(
                    "Application status must be ACCEPTED or REJECTED");
        }

        // ================= ACCEPT APPLICATION =================

        if (status == ApplicationStatus.ACCEPTED) {

            boolean alreadyMember =
                    projectMemberRepository
                            .existsByProjectAndStudent(
                                    project,
                                    application.getStudent());

            if (alreadyMember) {

                throw new ConflictException(
                        "Student is already a project member");
            }

            long currentMembers =
                    projectMemberRepository
                            .countByProjectAndStatus(
                                    project,
                                    ProjectMemberStatus.ACTIVE);

            if (currentMembers >=
                    project.getDesiredTeamSize()) {

                throw new BadRequestException(
                        "Project team is already full");
            }

            ProjectMember projectMember =
                    new ProjectMember();

            projectMember.setProject(project);

            projectMember.setStudent(
                    application.getStudent());

            projectMember.setJoinedAt(
                    LocalDateTime.now());

            projectMember.setRole(
                    ProjectMemberRole.MEMBER);

            projectMember.setStatus(
                    ProjectMemberStatus.ACTIVE);

            projectMemberRepository.save(
                    projectMember);
        }

        // Update application status

        application.setStatus(status);

        applicationRepository.save(application);

        // ================= NOTIFICATION =================

        if (status == ApplicationStatus.ACCEPTED) {

            notificationService.createNotification(
                    application.getStudent(),
                    NotificationType.APPLICATION_ACCEPTED,
                    "Congratulations! Your application for the project "
                            + project.getName()
                            + " has been accepted.");

        } else {

            notificationService.createNotification(
                    application.getStudent(),
                    NotificationType.APPLICATION_REJECTED,
                    "Your application for the project "
                            + project.getName()
                            + " was rejected. Better luck next time.");
        }

        return new ApiResponse(
                true,
                "Application status updated successfully");
    }

    // ================= HELPER METHOD =================

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
}