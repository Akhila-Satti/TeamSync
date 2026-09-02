package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Invitation;

public interface InvitationRepository extends JpaRepository<Invitation, Integer>{
    
}
