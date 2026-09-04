package com.projects.teamsync.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.StudentSkillRequest;
import com.projects.teamsync.dto.UpdateStudentSkill;
import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.entity.StudentSkill;
import com.projects.teamsync.exception.BadRequestException;
import com.projects.teamsync.exception.ConflictException;
import com.projects.teamsync.exception.AccessDeniedException;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;
import com.projects.teamsync.repository.SkillRepository;
import com.projects.teamsync.repository.StudentRepository;
import com.projects.teamsync.repository.StudentSkillRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentSkillService {

    private StudentRepository studentRepository;
    private StudentSkillRepository studentSkillRepository;
    private SkillRepository skillRepository;

    public StudentSkillService(
            StudentRepository studentRepository,
            StudentSkillRepository studentSkillRepository,
            SkillRepository skillRepository) {

        this.studentRepository =
                studentRepository;

        this.studentSkillRepository =
                studentSkillRepository;

        this.skillRepository =
                skillRepository;
    }

    private Student getAuthenticatedStudent() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {
            throw new UnauthorizedException(
                    "Authenticated user not found");
        }

        return student;
    }

    public ApiResponse addStudentSkills(
            StudentSkillRequest request) {

        if (request == null) {
            throw new BadRequestException(
                    "Invalid skill request");
        }

        Student student =
                getAuthenticatedStudent();

        Integer skillId =
                request.getSkillId();

        Integer proficiency =
                request.getProficiency();

        if (skillId == null) {
            throw new BadRequestException(
                    "Skill ID is required");
        }

        if (proficiency == null ||
                proficiency < 1 ||
                proficiency > 5) {

            throw new BadRequestException(
                    "Proficiency must be between 1 and 5");
        }

        Skill skill =
                skillRepository
                        .findById(skillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Skill not found"));

        boolean alreadyExists =
                studentSkillRepository
                        .existsByStudentAndSkill(
                                student,
                                skill);

        if (alreadyExists) {
            throw new ConflictException(
                    "Skill already added to student");
        }

        StudentSkill studentSkill =
                new StudentSkill();

        studentSkill.setStudent(student);
        studentSkill.setSkill(skill);
        studentSkill.setProficiency(
                proficiency);

        studentSkillRepository.save(
                studentSkill);

        return new ApiResponse(
                true,
                "Skill added successfully");
    }

    public ApiResponse updateStudentSkill(
            Integer studentSkillId,
            UpdateStudentSkill request) {

        if (request == null) {
            throw new BadRequestException(
                    "Update request is required");
        }

        Student student =
                getAuthenticatedStudent();

        StudentSkill studentSkill =
                studentSkillRepository
                        .findById(studentSkillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student skill not found"));

        if (!studentSkill
        .getStudent()
        .getId()
        .equals(student.getId())) {

    throw new AccessDeniedException(
            "You are not authorized to update this skill");
}

        Integer proficiency =
                request.getProficiency();

        if (proficiency == null ||
                proficiency < 1 ||
                proficiency > 5) {

            throw new BadRequestException(
                    "Proficiency must be between 1 and 5");
        }

        studentSkill.setProficiency(
                proficiency);

        studentSkillRepository.save(
                studentSkill);

        return new ApiResponse(
                true,
                "Skill updated successfully");
    }

    public ApiResponse deleteStudentSkill(
            Integer studentSkillId) {

        Student student =
                getAuthenticatedStudent();

        StudentSkill studentSkill =
                studentSkillRepository
                        .findById(studentSkillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student skill not found"));

        if (!studentSkill
        .getStudent()
        .getId()
        .equals(student.getId())) {

    throw new AccessDeniedException(
            "You are not authorized to delete this skill");
}

        studentSkillRepository.delete(
                studentSkill);

        return new ApiResponse(
                true,
                "Skill deleted successfully");
    }
}