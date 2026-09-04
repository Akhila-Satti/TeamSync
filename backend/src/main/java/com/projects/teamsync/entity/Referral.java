package com.projects.teamsync.entity;

import java.time.LocalDateTime;

import com.projects.teamsync.enums.ReferralStatus;

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
public class Referral {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="project_id",nullable=false)
    private Project project;

    @ManyToOne
    @JoinColumn(name="referred_by",nullable=false)
    private Student referredBy;

    @ManyToOne
    @JoinColumn(name="referred_student",nullable=false)
    private Student referredStudent;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ReferralStatus status;

    @Column(nullable=false)
    private LocalDateTime createdAt;
}
