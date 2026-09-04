package com.projects.teamsync.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.ProjectSkillRequest;
import com.projects.teamsync.dto.UpdateProjectSkill;

import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectSkill;
import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.entity.Student;

import com.projects.teamsync.enums.ProjectStatus;

import com.projects.teamsync.exception.BadRequestException;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;
import com.projects.teamsync.exception.AccessDeniedException;

import com.projects.teamsync.repository.ProjectRepository;
import com.projects.teamsync.repository.ProjectSkillRepository;
import com.projects.teamsync.repository.SkillRepository;
import com.projects.teamsync.repository.StudentRepository;

@Service
public class ProjectSkillService {

    private ProjectSkillRepository projectSkillRepository;
    private ProjectRepository projectRepository;
    private SkillRepository skillRepository;
    private StudentRepository studentRepository;


    public ProjectSkillService(
            ProjectSkillRepository projectSkillRepository,
            ProjectRepository projectRepository,
            SkillRepository skillRepository,
            StudentRepository studentRepository) {

        this.projectSkillRepository =
                projectSkillRepository;

        this.projectRepository =
                projectRepository;

        this.skillRepository =
                skillRepository;

        this.studentRepository =
                studentRepository;
    }



    // ================= ADD SKILL =================

    public ApiResponse addProjectSkill(
            Integer projectId,
            ProjectSkillRequest request) {


        if (request == null) {

            throw new BadRequestException(
                    "Invalid request");
        }


        Student student =
                getAuthenticatedStudent();


        Project project =
                projectRepository
                        .findById(projectId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project not found"));


        validateProjectOwner(
                project,
                student);


        validateProjectOpen(project);


        if (request.getSkillId() == null) {

            throw new BadRequestException(
                    "Skill ID is required");
        }


        if (request.getImportance() == null) {

            throw new BadRequestException(
                    "Importance is required");
        }


        if (request.getImportance() < 1 ||
                request.getImportance() > 5) {

            throw new BadRequestException(
                    "Importance must be between 1 and 5");
        }


        Skill skill =
                skillRepository
                        .findById(
                                request.getSkillId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Skill not found"));


        boolean alreadyExists =
                projectSkillRepository
                        .existsByProjectAndSkill(
                                project,
                                skill);


        if (alreadyExists) {

            throw new BadRequestException(
                    "Skill is already added to this project");
        }


        ProjectSkill projectSkill =
                new ProjectSkill();


        projectSkill.setProject(project);

        projectSkill.setSkill(skill);

        projectSkill.setImportance(
                request.getImportance());


        projectSkillRepository.save(
                projectSkill);


        return new ApiResponse(
                true,
                "Project skill added successfully");
    }



    // ================= UPDATE SKILL =================

    public ApiResponse updateProjectSkill(
            Integer projectSkillId,
            UpdateProjectSkill request) {


        if (request == null) {

            throw new BadRequestException(
                    "Invalid request");
        }


        Student student =
                getAuthenticatedStudent();


        ProjectSkill projectSkill =
                projectSkillRepository
                        .findById(projectSkillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project skill not found"));


        Project project =
                projectSkill.getProject();


        validateProjectOwner(
                project,
                student);


        validateProjectOpen(project);


        if (request.getImportance() == null) {

            throw new BadRequestException(
                    "Importance is required");
        }


        if (request.getImportance() < 1 ||
                request.getImportance() > 5) {

            throw new BadRequestException(
                    "Importance must be between 1 and 5");
        }


        projectSkill.setImportance(
                request.getImportance());


        projectSkillRepository.save(
                projectSkill);


        return new ApiResponse(
                true,
                "Skill importance updated successfully");
    }



    // ================= DELETE SKILL =================

    public ApiResponse deleteProjectSkill(
            Integer projectSkillId) {


        Student student =
                getAuthenticatedStudent();


        ProjectSkill projectSkill =
                projectSkillRepository
                        .findById(projectSkillId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Project skill not found"));


        Project project =
                projectSkill.getProject();


        validateProjectOwner(
                project,
                student);


        validateProjectOpen(project);


        long skillCount =
                projectSkillRepository
                        .countByProject(project);


        if (skillCount <= 1) {

            throw new BadRequestException(
                    "A project must have at least one skill");
        }


        projectSkillRepository.delete(
                projectSkill);


        return new ApiResponse(
                true,
                "Project skill deleted successfully");
    }



    // ================= HELPER METHODS =================


    private Student getAuthenticatedStudent() {


        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();


        Student student =
                studentRepository
                        .findByEmail(email);


        if (student == null) {

            throw new UnauthorizedException(
                    "Authenticated user not found");
        }


        return student;
    }



    private void validateProjectOwner(
        Project project,
        Student student) {

    if (!project.getCreatedBy()
            .getId()
            .equals(student.getId())) {

        throw new AccessDeniedException(
                "You are not authorized to modify this project");
    }
}



    private void validateProjectOpen(
            Project project) {


        if (project.getStatus() ==
                ProjectStatus.CLOSED) {


            throw new BadRequestException(
                    "Skills of a closed project cannot be modified");
        }
    }
}