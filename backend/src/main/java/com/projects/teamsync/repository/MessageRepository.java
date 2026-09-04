package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Message;
import com.projects.teamsync.entity.ProjectRoom;

import java.util.List;


public interface MessageRepository extends JpaRepository<Message, Integer>{
    List<Message> findByRoomOrderByCreatedAtAsc(ProjectRoom room);
}
