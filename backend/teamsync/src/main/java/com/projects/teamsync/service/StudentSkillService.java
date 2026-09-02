package com.projects.teamsync.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.StudentSkillRequest;
import com.projects.teamsync.dto.StudentSkillSet;
import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.entity.StudentSkill;
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

    this.studentRepository = studentRepository;
    this.studentSkillRepository = studentSkillRepository;
    this.skillRepository = skillRepository;
}

public ApiResponse addStudentSkills(List<StudentSkillRequest> skills) {

    if (skills == null || skills.isEmpty()) {
        return new ApiResponse(false, "Invalid skills set");
    }

    String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    Student student = studentRepository.findByEmail(email);

    if (student == null) {
        return new ApiResponse(false, "No student found");
    }

    HashSet<Integer> userSkillIds = new HashSet<>();
    List<StudentSkillSet> validatedSkills = new ArrayList<>();

    // Validate all skills first
    for (StudentSkillRequest studentSkillRequest : skills) {

        Integer skillId = studentSkillRequest.getSkillId();
        Integer proficiency = studentSkillRequest.getProficiency();

        if (skillId == null) {
            return new ApiResponse(false, "Invalid skill id");
        }

        if (proficiency == null || proficiency < 1 || proficiency > 5) {
            return new ApiResponse(false, "Invalid proficiency range");
        }

        Skill skill = skillRepository
                .findById(skillId)
                .orElse(null);

        if (skill == null) {
            return new ApiResponse(false, "No such skill exists");
        }

        // Prevent duplicate skills within the current request
        if (userSkillIds.contains(skillId)) {
            continue;
        }

        // Check if student already has this skill
        boolean alreadyExists =
                studentSkillRepository
                        .existsByStudentAndSkill(student, skill);

        if (alreadyExists) {
            return new ApiResponse(false, "Skill already added to student");
        }

        userSkillIds.add(skillId);

        StudentSkillSet studentSkillSet =
                new StudentSkillSet();

        studentSkillSet.setSkill(skill);
        studentSkillSet.setProficiency(proficiency);

        validatedSkills.add(studentSkillSet);
    }

    // Save only after all validation succeeds
    for (StudentSkillSet studentSkillSet : validatedSkills) {

        StudentSkill studentSkill =
                new StudentSkill();

        studentSkill.setStudent(student);
        studentSkill.setSkill(studentSkillSet.getSkill());
        studentSkill.setProficiency(
                studentSkillSet.getProficiency()
        );

        studentSkillRepository.save(studentSkill);
    }

    return new ApiResponse(
            true,
            "Added all skills successfully"
    );
}


}
