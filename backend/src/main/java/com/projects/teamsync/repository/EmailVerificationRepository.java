package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.EmailVerification;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.enums.OtpPurpose;


public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer>{

    EmailVerification findByStudentAndPurposeAndVerifiedFalse(
        Student student,
        OtpPurpose purpose
);
EmailVerification findByStudentAndPurposeAndVerifiedTrue(
        Student student,
        OtpPurpose purpose
);
    
}
