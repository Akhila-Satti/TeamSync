package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Skill;



public interface SkillRepository extends JpaRepository<Skill, Integer> {

    boolean existsByName(String skillName);
    
}
