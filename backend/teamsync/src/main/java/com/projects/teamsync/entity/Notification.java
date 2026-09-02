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
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private Student recipient;

    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private Boolean isRead=false;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
