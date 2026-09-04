package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Notification;
import com.projects.teamsync.entity.Student;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Integer>{
    List<Notification> findByRecipientOrderByCreatedAtDesc(Student recipient);
}
