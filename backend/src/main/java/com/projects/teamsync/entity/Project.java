package com.projects.teamsync.entity;

import java.time.LocalDateTime;

import com.projects.teamsync.enums.ProjectStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Student createdBy;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false)
    private Integer desiredTeamSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ProjectStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime closedAt;

}
