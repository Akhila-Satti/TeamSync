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

@Data
@Entity
public class Message {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="room_id",nullable=false)
    private ProjectRoom room;

    @ManyToOne
    @JoinColumn(name="student_id",nullable=false)
    private Student sender;

    @Column(nullable=false)
    private String content;

    @Column(nullable=false)
    private LocalDateTime createdAt;


}
