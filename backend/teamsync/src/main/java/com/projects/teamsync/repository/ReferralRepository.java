package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Referral;

public interface ReferralRepository  extends JpaRepository<Referral, Integer>{
    
}
