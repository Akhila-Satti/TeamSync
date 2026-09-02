package com.projects.teamsync.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class ProjectRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name="project_id",nullable=false,unique=true)
    private Project project;

    @Column(nullable=false)
    private LocalDateTime createdAt;
}
