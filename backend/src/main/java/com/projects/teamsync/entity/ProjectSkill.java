package com.projects.teamsync.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class ProjectSkill {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name="project_id",nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name="skill_id",nullable = false)
    private Skill skill;

    private Integer importance;
}
