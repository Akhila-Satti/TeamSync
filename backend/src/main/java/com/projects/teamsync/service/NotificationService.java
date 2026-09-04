package com.projects.teamsync.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.NotificationResponse;
import com.projects.teamsync.entity.Notification;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.enums.NotificationType;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;
import com.projects.teamsync.repository.NotificationRepository;
import com.projects.teamsync.repository.StudentRepository;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;
    private StudentRepository studentRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            StudentRepository studentRepository) {

        this.notificationRepository =
                notificationRepository;

        this.studentRepository =
                studentRepository;
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

    public Notification createNotification(
            Student student,
            NotificationType type,
            String message) {

        Notification notification =
                new Notification();

        notification.setCreatedAt(
                LocalDateTime.now());

        notification.setIsRead(false);

        notification.setType(type);

        notification.setMessage(message);

        notification.setRecipient(student);

        return notificationRepository
                .save(notification);
    }

    public List<NotificationResponse>
            getMyNotifications() {

        Student student =
                getAuthenticatedStudent();

        List<Notification> notifications =
                notificationRepository
                        .findByRecipientOrderByCreatedAtDesc(
                                student);

        List<NotificationResponse> responses =
                new ArrayList<>();

        for (Notification notification :
                notifications) {

            NotificationResponse response =
                    new NotificationResponse();

            response.setNotificationId(
                    notification.getId());

            response.setMessage(
                    notification.getMessage());

            response.setType(
                    notification.getType());

            response.setRead(
                    notification.getIsRead());

            response.setCreatedAt(
                    notification.getCreatedAt());

            responses.add(response);
        }

        return responses;
    }

    public ApiResponse markNotificationAsRead(
            Integer notificationId) {

        Student student =
                getAuthenticatedStudent();

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"));

        if (!notification
                .getRecipient()
                .getId()
                .equals(student.getId())) {

            throw new UnauthorizedException(
                    "Not authorized to update this notification");
        }

        if (notification.getIsRead()) {

            return new ApiResponse(
                    true,
                    "Notification is already marked as read");
        }

        notification.setIsRead(true);

        notificationRepository
                .save(notification);

        return new ApiResponse(
                true,
                "Notification marked as read");
    }
}