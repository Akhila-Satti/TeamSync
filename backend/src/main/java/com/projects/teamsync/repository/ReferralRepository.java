package com.projects.teamsync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.Referral;
import com.projects.teamsync.entity.Student;

public interface ReferralRepository  extends JpaRepository<Referral, Integer>{
    boolean existsByProjectAndReferredStudent(Project project,Student student);
    List<Referral> findByProject(Project project);
}
