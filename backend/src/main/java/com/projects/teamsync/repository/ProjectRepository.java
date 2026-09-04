package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.Student;

import java.util.List;
import com.projects.teamsync.enums.ProjectStatus;


public interface ProjectRepository extends JpaRepository<Project, Integer>{
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findByCreatedBy(Student student);
}
