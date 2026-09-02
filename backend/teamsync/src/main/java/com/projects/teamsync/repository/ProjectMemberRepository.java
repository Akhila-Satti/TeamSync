package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.ProjectMember;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Integer>{
    
}
