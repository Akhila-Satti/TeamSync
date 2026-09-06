package com.projects.teamsync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectRoom;

public interface ProjectRoomRepository
        extends JpaRepository<ProjectRoom, Integer> {

    Optional<ProjectRoom> findByProject(Project project);
}