package com.projects.teamsync.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.InvitationResponse;
import com.projects.teamsync.dto.InvitationStatusRequest;
import com.projects.teamsync.dto.SendInvitationRequest;

import com.projects.teamsync.entity.Invitation;
import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectMember;
import com.projects.teamsync.entity.ProjectSkill;
import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.entity.Student;

import com.projects.teamsync.enums.InvitationStatus;
import com.projects.teamsync.enums.NotificationType;
import com.projects.teamsync.enums.ProjectMemberRole;
import com.projects.teamsync.enums.ProjectMemberStatus;
import com.projects.teamsync.enums.ProjectStatus;

import com.projects.teamsync.exception.BadRequestException;
import com.projects.teamsync.exception.ConflictException;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;

import com.projects.teamsync.repository.InvitationRepository;
import com.projects.teamsync.repository.ProjectMemberRepository;
import com.projects.teamsync.repository.ProjectRepository;
import com.projects.teamsync.repository.ProjectSkillRepository;
import com.projects.teamsync.repository.StudentRepository;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class InvitationService {


    private InvitationRepository invitationRepository;

    private StudentRepository studentRepository;

    private ProjectRepository projectRepository;

    private ProjectMemberRepository projectMemberRepository;

    private ProjectSkillRepository projectSkillRepository;

    private NotificationService notificationService;


    public InvitationService(

            InvitationRepository invitationRepository,

            StudentRepository studentRepository,

            ProjectRepository projectRepository,

            ProjectMemberRepository projectMemberRepository,

            ProjectSkillRepository projectSkillRepository,

            NotificationService notificationService) {


        this.invitationRepository =
                invitationRepository;

        this.projectMemberRepository =
                projectMemberRepository;

        this.studentRepository =
                studentRepository;

        this.projectRepository =
                projectRepository;

        this.projectSkillRepository =
                projectSkillRepository;

        this.notificationService =
                notificationService;
    }


    // ================= SEND INVITATION =================

    public ApiResponse sendInvitation(

            Integer projectId,

            SendInvitationRequest request) {


        if (request == null ||
                request.getStudentId() == null) {

            throw new BadRequestException(
                    "Student ID is required");
        }


        Student creator =
                getAuthenticatedStudent();


        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"));


        if (!project.getCreatedBy()
                .getId()
                .equals(creator.getId())) {

            throw new UnauthorizedException(
                    "You are not authorized to send invitations for this project");
        }


        if (project.getStatus() ==
                ProjectStatus.CLOSED) {

            throw new BadRequestException(
                    "Cannot send invitations for a closed project");
        }


        Student invited =
                studentRepository
                        .findById(
                                request.getStudentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found"));


        if (invited.getId()
                .equals(creator.getId())) {

            throw new BadRequestException(
                    "Project creator cannot invite themselves");
        }


        long count =
                projectMemberRepository
                        .countByProjectAndStatus(

                                project,

                                ProjectMemberStatus.ACTIVE);


        if (count >=
                project.getDesiredTeamSize()) {

            throw new BadRequestException(
                    "Project team is already full");
        }


        boolean alreadyMember =
                projectMemberRepository
                        .existsByProjectAndStudent(

                                project,

                                invited);


        if (alreadyMember) {

            throw new ConflictException(
                    "Student is already a member of this project");
        }


        boolean alreadyInvited =
                invitationRepository
                        .existsByProjectAndSentTo(

                                project,

                                invited);


        if (alreadyInvited) {

            throw new ConflictException(
                    "Student has already been invited to this project");
        }


        Invitation invite =
                new Invitation();


        invite.setProject(project);

        invite.setSentTo(invited);

        invite.setStatus(
                InvitationStatus.PENDING);

        invite.setCreatedAt(
                LocalDateTime.now());


        invitationRepository.save(
                invite);


        // Notify invited student

        notificationService
                .createNotification(

                        invited,

                        NotificationType.INVITATION_RECEIVED,

                        "You have received an invitation to join the project "
                                + project.getName()
                                + "."
                );


        return new ApiResponse(

                true,

                "Invitation sent successfully"
        );
    }


    // ================= GET MY INVITATIONS =================

    public List<InvitationResponse>
            getMyInvitations() {


        Student student =
                getAuthenticatedStudent();


        List<Invitation> invitations =
                invitationRepository
                        .findBySentTo(
                                student);


        List<InvitationResponse>
                invites =
                new ArrayList<>();


        for (Invitation invitation :
                invitations) {


            InvitationResponse invited =
                    new InvitationResponse();


            invited.setDescription(

                    invitation.getProject()
                            .getDescription()
            );


            invited.setInvitationId(

                    invitation.getId()
            );


            invited.setProjectId(

                    invitation.getProject()
                            .getId()
            );


            invited.setProjectName(

                    invitation.getProject()
                            .getName()
            );


            invited.setProjectStatus(

                    invitation.getProject()
                            .getStatus()
            );


            invited.setStatus(

                    invitation.getStatus()
            );


            invited.setSentBy(

                    invitation.getProject()
                            .getCreatedBy()
                            .getUserName()
            );


            invited.setSentAt(

                    invitation.getCreatedAt()
            );


            List<ProjectSkill>
                    projectSkills =

                    projectSkillRepository
                            .findByProject(

                                    invitation
                                            .getProject()
                            );


            List<Skill>
                    skills =
                    new ArrayList<>();


            for (ProjectSkill projSkill :
                    projectSkills) {


                skills.add(

                        projSkill
                                .getSkill()
                );
            }


            invited.setProjectSkills(
                    skills);


            invites.add(
                    invited);
        }


        return invites;
    }


    // ================= UPDATE INVITATION STATUS =================

    public ApiResponse
            updateInvitationStatus(

                    Integer invitationId,

                    InvitationStatusRequest request) {


        if (request == null ||
                request.getStatus() == null) {

            throw new BadRequestException(
                    "Invitation status is required");
        }


        Student student =
                getAuthenticatedStudent();


        Invitation invitation =
                invitationRepository
                        .findById(
                                invitationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Invitation not found"));


        Project project =
                invitation.getProject();


        if (!invitation.getSentTo()
                .getId()
                .equals(student.getId())) {

            throw new UnauthorizedException(
                    "You are not authorized to respond to this invitation");
        }


        if (project.getStatus() ==
                ProjectStatus.CLOSED) {

            throw new BadRequestException(
                    "Cannot respond to an invitation for a closed project");
        }


        if (invitation.getStatus()
                != InvitationStatus.PENDING) {

            throw new BadRequestException(
                    "This invitation has already been responded to");
        }


        InvitationStatus status =
                request.getStatus();


        if (status !=
                InvitationStatus.ACCEPTED

                &&

                status !=
                InvitationStatus.REJECTED) {

            throw new BadRequestException(
                    "Invitation status must be ACCEPTED or REJECTED");
        }


        // ================= ACCEPT INVITATION =================

        if (status ==
                InvitationStatus.ACCEPTED) {


            boolean alreadyMember =
                    projectMemberRepository
                            .existsByProjectAndStudent(

                                    project,

                                    invitation.getSentTo());


            if (alreadyMember) {

                throw new ConflictException(
                        "You are already a member of this project");
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


            projectMember.setProject(
                    project);


            projectMember.setStudent(
                    invitation.getSentTo());


            projectMember.setJoinedAt(
                    LocalDateTime.now());


            projectMember.setRole(
                    ProjectMemberRole.MEMBER);


            projectMember.setStatus(
                    ProjectMemberStatus.ACTIVE);


            projectMemberRepository.save(
                    projectMember);
        }


        // Update invitation

        invitation.setStatus(
                status);


        invitation.setRespondedAt(
                LocalDateTime.now());


        invitationRepository.save(
                invitation);


        // ================= NOTIFICATION =================

        if (status ==
                InvitationStatus.ACCEPTED) {


            notificationService
                    .createNotification(

                            project.getCreatedBy(),

                            NotificationType.INVITATION_ACCEPTED,

                            student.getUserName()
                                    + " accepted your invitation to join the project "
                                    + project.getName()
                                    + "."
                    );

        } else {


            notificationService
                    .createNotification(

                            project.getCreatedBy(),

                            NotificationType.INVITATION_REJECTED,

                            student.getUserName()
                                    + " rejected your invitation to join the project "
                                    + project.getName()
                                    + "."
                    );
        }


        return new ApiResponse(

                true,

                "Invitation status updated successfully"
        );
    }


    // ================= HELPER METHOD =================

    private Student
            getAuthenticatedStudent() {


        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();


        Student student =
                studentRepository
                        .findByEmail(
                                email);


        if (student == null) {

            throw new UnauthorizedException(
                    "Authenticated user not found");
        }


        return student;
    }
}