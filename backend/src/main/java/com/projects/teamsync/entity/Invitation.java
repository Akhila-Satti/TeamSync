package com.projects.teamsync.entity;


import java.time.LocalDateTime;

import com.projects.teamsync.enums.InvitationStatus;

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
public class Invitation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="project_id",nullable=false)
    private Project project;

    @ManyToOne
    @JoinColumn(name="sent_to",nullable=false)
    private Student sentTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private InvitationStatus status;

    @Column(nullable=false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime respondedAt;

}
