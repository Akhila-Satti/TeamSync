package com.projects.teamsync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectSkill;
import com.projects.teamsync.entity.Skill;

public interface ProjectSkillRepository extends JpaRepository<ProjectSkill, Integer>{
  

    List<ProjectSkill> findByProject(Project project);
    boolean existsByProjectAndSkill(Project project, Skill skill);
    long countByProject(Project project);
    

}
