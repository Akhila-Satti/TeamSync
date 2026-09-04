package com.projects.teamsync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Invitation;
import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.Student;

public interface InvitationRepository extends JpaRepository<Invitation, Integer> {

    boolean existsByProjectAndSentTo(Project project, Student student);

    List<Invitation> findBySentTo(Student student);

    
}