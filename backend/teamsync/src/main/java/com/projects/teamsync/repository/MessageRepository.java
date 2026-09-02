package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer>{
    
}
