package com.projects.teamsync.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class ProjectMember {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="project_id",nullable=false)
    private Project project;

    @ManyToOne
    @JoinColumn(name="student_id",nullable=false)
    private Student student;

    @Column(nullable=false)
    private String role;

    @Column(nullable=false)
    private String status;

    @Column(nullable=false)
    private LocalDateTime joinedAt;

    private LocalDateTime leftAt;

}
