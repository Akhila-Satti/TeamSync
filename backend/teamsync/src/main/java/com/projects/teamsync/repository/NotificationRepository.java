package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer>{
    
}
