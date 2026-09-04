package com.projects.teamsync.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.CreateReferralRequest;
import com.projects.teamsync.dto.ReferralResponse;
import com.projects.teamsync.dto.ReferralStatusRequest;
import com.projects.teamsync.entity.Invitation;
import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.Referral;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.enums.InvitationStatus;
import com.projects.teamsync.enums.NotificationType;
import com.projects.teamsync.enums.ProjectMemberStatus;
import com.projects.teamsync.enums.ProjectStatus;
import com.projects.teamsync.enums.ReferralStatus;
import com.projects.teamsync.exception.BadRequestException;
import com.projects.teamsync.exception.ConflictException;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;
import com.projects.teamsync.repository.ApplicationRepository;
import com.projects.teamsync.repository.InvitationRepository;
import com.projects.teamsync.repository.ProjectMemberRepository;
import com.projects.teamsync.repository.ProjectRepository;
import com.projects.teamsync.repository.ReferralRepository;
import com.projects.teamsync.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReferralService {

    private ReferralRepository referralRepository;
    private ProjectRepository projectRepository;
    private StudentRepository studentRepository;
    private ProjectMemberRepository projectMemberRepository;
    private ApplicationRepository applicationRepository;
    private InvitationRepository invitationRepository;
    private NotificationService notificationService;

    public ReferralService(
            ReferralRepository referralRepository,
            ProjectRepository projectRepository,
            StudentRepository studentRepository,
            ProjectMemberRepository projectMemberRepository,
            ApplicationRepository applicationRepository,
            InvitationRepository invitationRepository,
            NotificationService notificationService) {

        this.referralRepository =
                referralRepository;

        this.projectRepository =
                projectRepository;

        this.studentRepository =
                studentRepository;

        this.projectMemberRepository =
                projectMemberRepository;

        this.applicationRepository =
                applicationRepository;

        this.invitationRepository =
                invitationRepository;

        this.notificationService =
                notificationService;
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

    private Project getProject(
            Integer projectId) {

        return projectRepository
                .findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"));
    }

    private Referral getReferral(
            Integer referralId) {

        return referralRepository
                .findById(referralId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Referral not found"));
    }

    private void validateProjectCreator(
            Project project,
            Student student) {

        if (!project.getCreatedBy()
                .getId()
                .equals(student.getId())) {

            throw new UnauthorizedException(
                    "Not authorized for this project");
        }
    }

    private void validateProjectOpen(
            Project project) {

        if (project.getStatus()
                != ProjectStatus.OPEN) {

            throw new ConflictException(
                    "Project is closed");
        }
    }

    public ApiResponse createReferral(
            Integer projectId,
            CreateReferralRequest request) {

        if (request == null
                || request.getReferredStudentId() == null) {

            throw new BadRequestException(
                    "Referred student ID is required");
        }

        Student student =
                getAuthenticatedStudent();

        Project project =
                getProject(projectId);

        validateProjectOpen(project);

        Student referredStudent =
                studentRepository
                        .findById(
                                request.getReferredStudentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Referred student not found"));

        if (student.getId()
                .equals(referredStudent.getId())) {

            throw new BadRequestException(
                    "Self-referral is not allowed");
        }

        if (project.getCreatedBy()
                .getId()
                .equals(student.getId())) {

            throw new BadRequestException(
                    "Project creator cannot create a referral");
        }

        boolean isActive =
                projectMemberRepository
                        .existsByProjectAndStudentAndStatus(
                                project,
                                student,
                                ProjectMemberStatus.ACTIVE);

        if (!isActive) {

            throw new UnauthorizedException(
                    "You must be an active project member to create a referral");
        }

        boolean isMember =
                projectMemberRepository
                        .existsByProjectAndStudent(
                                project,
                                referredStudent);

        if (isMember) {

            throw new ConflictException(
                    "Student is already associated with this project");
        }

        boolean isApplied =
                applicationRepository
                        .existsByStudentAndProject(
                                referredStudent,
                                project);

        if (isApplied) {

            throw new ConflictException(
                    "Student has already applied to this project");
        }

        boolean isInvited =
                invitationRepository
                        .existsByProjectAndSentTo(
                                project,
                                referredStudent);

        if (isInvited) {

            throw new ConflictException(
                    "Student has already been invited to this project");
        }

        boolean isReferred =
                referralRepository
                        .existsByProjectAndReferredStudent(
                                project,
                                referredStudent);

        if (isReferred) {

            throw new ConflictException(
                    "Student has already been referred to this project");
        }

        Referral referral =
                new Referral();

        referral.setCreatedAt(
                LocalDateTime.now());

        referral.setProject(project);

        referral.setReferredBy(student);

        referral.setReferredStudent(
                referredStudent);

        referral.setStatus(
                ReferralStatus.PENDING);

        referralRepository.save(referral);

        notificationService
                .createNotification(
                        project.getCreatedBy(),
                        NotificationType.REFERRAL_RECEIVED,
                        student.getUserName()
                                + " referred "
                                + referredStudent.getUserName()
                                + " for your project "
                                + project.getName()
                                + ".");

        return new ApiResponse(
                true,
                "Referral created successfully");
    }

    public List<ReferralResponse>
            getProjectReferrals(
                    Integer projectId) {

        Student student =
                getAuthenticatedStudent();

        Project project =
                getProject(projectId);

        validateProjectCreator(
                project,
                student);

        List<Referral> referrals =
                referralRepository
                        .findByProject(project);

        List<ReferralResponse> responses =
                new ArrayList<>();

        for (Referral referral : referrals) {

            responses.add(
                    convertToResponse(referral));
        }

        return responses;
    }

    public ApiResponse updateReferralStatus(
            Integer referralId,
            ReferralStatusRequest request) {

        if (request == null
                || request.getReferralStatus() == null) {

            throw new BadRequestException(
                    "Referral status is required");
        }

        ReferralStatus status =
                request.getReferralStatus();

        if (status != ReferralStatus.ACCEPTED
                && status != ReferralStatus.REJECTED) {

            throw new BadRequestException(
                    "Invalid referral status");
        }

        Student student =
                getAuthenticatedStudent();

        Referral referral =
                getReferral(referralId);

        Project project =
                referral.getProject();

        validateProjectCreator(
                project,
                student);

        validateProjectOpen(project);

        if (referral.getStatus()
                != ReferralStatus.PENDING) {

            throw new ConflictException(
                    "Referral is no longer pending");
        }

        if (status
                == ReferralStatus.ACCEPTED) {

            return acceptReferral(
                    referral,
                    project);
        }

        return rejectReferral(
                referral,
                project);
    }

    private ApiResponse acceptReferral(
            Referral referral,
            Project project) {

        Student referredStudent =
                referral.getReferredStudent();

        boolean isMember =
                projectMemberRepository
                        .existsByProjectAndStudent(
                                project,
                                referredStudent);

        if (isMember) {

            throw new ConflictException(
                    "Student is already associated with this project");
        }

        boolean isApplied =
                applicationRepository
                        .existsByStudentAndProject(
                                referredStudent,
                                project);

        if (isApplied) {

            throw new ConflictException(
                    "Student has already applied to this project");
        }

        boolean isInvited =
                invitationRepository
                        .existsByProjectAndSentTo(
                                project,
                                referredStudent);

        if (isInvited) {

            throw new ConflictException(
                    "Student has already been invited to this project");
        }

        long currentMembers =
                projectMemberRepository
                        .countByProjectAndStatus(
                                project,
                                ProjectMemberStatus.ACTIVE);

        if (currentMembers
                >= project.getDesiredTeamSize()) {

            throw new ConflictException(
                    "Project team is already full");
        }

        referral.setStatus(
                ReferralStatus.ACCEPTED);

        referralRepository.save(referral);

        Invitation invitation =
                new Invitation();

        invitation.setCreatedAt(
                LocalDateTime.now());

        invitation.setProject(project);

        invitation.setSentTo(
                referredStudent);

        invitation.setStatus(
                InvitationStatus.PENDING);

        invitationRepository
                .save(invitation);

        notificationService
                .createNotification(
                        referral.getReferredBy(),
                        NotificationType.REFERRAL_ACCEPTED,
                        "Your referral of "
                                + referredStudent.getUserName()
                                + " for the project "
                                + project.getName()
                                + " was accepted.");

        notificationService
                .createNotification(
                        referredStudent,
                        NotificationType.INVITATION_RECEIVED,
                        "You received an invitation to join the project "
                                + project.getName()
                                + ".");

        return new ApiResponse(
                true,
                "Referral accepted and invitation created successfully");
    }

    private ApiResponse rejectReferral(
            Referral referral,
            Project project) {

        referral.setStatus(
                ReferralStatus.REJECTED);

        referralRepository.save(referral);

        notificationService
                .createNotification(
                        referral.getReferredBy(),
                        NotificationType.REFERRAL_REJECTED,
                        "Your referral of "
                                + referral
                                        .getReferredStudent()
                                        .getUserName()
                                + " for the project "
                                + project.getName()
                                + " was rejected.");

        return new ApiResponse(
                true,
                "Referral rejected successfully");
    }

    private ReferralResponse convertToResponse(
            Referral referral) {

        ReferralResponse response =
                new ReferralResponse();

        response.setReferralId(
                referral.getId());

        response.setProjectId(
                referral.getProject().getId());

        response.setProjectName(
                referral.getProject().getName());

        response.setReferredById(
                referral.getReferredBy().getId());

        response.setReferredByName(
                referral.getReferredBy().getUserName());

        response.setReferredStudentId(
                referral
                        .getReferredStudent()
                        .getId());

        response.setReferredStudentName(
                referral
                        .getReferredStudent()
                        .getUserName());

        response.setStatus(
                referral.getStatus());

        response.setCreatedAt(
                referral.getCreatedAt());

        return response;
    }
}