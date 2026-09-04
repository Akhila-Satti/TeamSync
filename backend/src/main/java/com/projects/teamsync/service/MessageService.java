package com.projects.teamsync.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.MessageResponse;
import com.projects.teamsync.dto.SendMessageRequest;
import com.projects.teamsync.entity.Message;
import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectRoom;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.enums.ProjectMemberStatus;
import com.projects.teamsync.exception.BadRequestException;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;
import com.projects.teamsync.repository.MessageRepository;
import com.projects.teamsync.repository.ProjectMemberRepository;
import com.projects.teamsync.repository.ProjectRoomRepository;
import com.projects.teamsync.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MessageService {

    private MessageRepository messageRepository;
    private ProjectRoomRepository projectRoomRepository;
    private StudentRepository studentRepository;
    private ProjectMemberRepository projectMemberRepository;

    public MessageService(
            MessageRepository messageRepository,
            ProjectRoomRepository projectRoomRepository,
            StudentRepository studentRepository,
            ProjectMemberRepository projectMemberRepository) {

        this.messageRepository =
                messageRepository;

        this.projectRoomRepository =
                projectRoomRepository;

        this.studentRepository =
                studentRepository;

        this.projectMemberRepository =
                projectMemberRepository;
    }

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

    private Student getStudentByEmail(
            String email) {

        Student student =
                studentRepository
                        .findByEmail(email);

        if (student == null) {

            throw new UnauthorizedException(
                    "Authenticated user not found");
        }

        return student;
    }

    private ProjectRoom getProjectRoom(
            Integer roomId) {

        return projectRoomRepository
                .findById(roomId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project room not found"));
    }

    private void validateProjectMembership(
            Project project,
            Student student) {

        boolean isActive =
                projectMemberRepository
                        .existsByProjectAndStudentAndStatus(
                                project,
                                student,
                                ProjectMemberStatus.ACTIVE);

        if (!isActive) {

            throw new UnauthorizedException(
                    "You are not an active member of this project");
        }
    }

    public MessageResponse sendMessage(
            Integer roomId,
            String email,
            SendMessageRequest request) {

        if (request == null) {

            throw new BadRequestException(
                    "Message request is required");
        }

        if (request.getContent() == null
                || request.getContent().isBlank()) {

            throw new BadRequestException(
                    "Message content cannot be empty");
        }

        Student student =
                getStudentByEmail(email);

        ProjectRoom projectRoom =
                getProjectRoom(roomId);

        Project project =
                projectRoom.getProject();

        validateProjectMembership(
                project,
                student);

        Message message =
                new Message();

        message.setRoom(projectRoom);

        message.setSender(student);

        message.setContent(
                request.getContent().strip());

        message.setCreatedAt(
                LocalDateTime.now());

        Message savedMessage =
                messageRepository.save(message);

        return convertToResponse(
                savedMessage);
    }

    public List<MessageResponse> getMessages(
            Integer roomId) {

        Student student =
                getAuthenticatedStudent();

        ProjectRoom projectRoom =
                getProjectRoom(roomId);

        Project project =
                projectRoom.getProject();

        validateProjectMembership(
                project,
                student);

        List<Message> messages =
                messageRepository
                        .findByRoomOrderByCreatedAtAsc(
                                projectRoom);

        List<MessageResponse> responses =
                new ArrayList<>();

        for (Message message : messages) {

            responses.add(
                    convertToResponse(message));
        }

        return responses;
    }

    private MessageResponse convertToResponse(
            Message message) {

        MessageResponse response =
                new MessageResponse();

        response.setRoomId(
                message.getRoom().getId());

        response.setContent(
                message.getContent());

        response.setSentAt(
                message.getCreatedAt());

        response.setStudentId(
                message.getSender().getId());

        response.setUserName(
                message.getSender().getUserName());

        return response;
    }
}