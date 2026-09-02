package com.projects.teamsync.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.ApplicationResponse;
import com.projects.teamsync.dto.ApplicationStatusRequest;
import com.projects.teamsync.entity.Application;
import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.entity.StudentSkill;
import com.projects.teamsync.enums.ApplicationStatus;
import com.projects.teamsync.enums.ProjectStatus;
import com.projects.teamsync.repository.ApplicationRepository;
import com.projects.teamsync.repository.ProjectRepository;
import com.projects.teamsync.repository.SkillRepository;
import com.projects.teamsync.repository.StudentRepository;
import com.projects.teamsync.repository.StudentSkillRepository;

@Service
public class ApplicationService {
    private ApplicationRepository applicationRepository;
    private ProjectRepository projectRepository;
    private StudentRepository studentRepository;
    private StudentSkillRepository studentSkillRepository;
    private SkillRepository skillRepository;

    public ApplicationService(ApplicationRepository applicationRepository, ProjectRepository projectRepository,
            StudentRepository studentRepository, SkillRepository skillRepository,
            StudentSkillRepository studentSkillRepository) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.projectRepository = projectRepository;
        this.skillRepository = skillRepository;
        this.studentSkillRepository = studentSkillRepository;

    }

    public ApiResponse createApplication(Integer projectId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentRepository.findByEmail(email);
        if (student == null) {
            return new ApiResponse(false, "No student found");
        }
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null || project.getStatus().equals(ProjectStatus.CLOSED)) {
            return new ApiResponse(false, "invalid project");
        }
        if (student.getId().equals(project.getCreatedBy().getId())) {
            return new ApiResponse(false, "Creator cannot apply");
        }

        boolean isapplied = applicationRepository.existsByStudentAndProject(student, project);
        if (isapplied) {
            return new ApiResponse(isapplied, "already applied");
        }
        Application application = new Application();
        application.setStudent(student);
        application.setProject(project);
        application.setStatus(ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());
        applicationRepository.save(application);

        return new ApiResponse(true, "Application created");

    }

    public List<ApplicationResponse> viewApplications(Integer projectId) {
        List<ApplicationResponse> allApplications = new ArrayList<>();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return allApplications;
        }
        if (!email.equals(project.getCreatedBy().getEmail())) {
            return allApplications;
        }

        List<Application> allApplied = applicationRepository.findByProject(project);
        for (Application app : allApplied) {
            ApplicationResponse appr = new ApplicationResponse();
            appr.setApplicantId(app.getStudent().getId());
            appr.setApplicationId(app.getId());
            appr.setDate(app.getCreatedAt());
            List<StudentSkill> studentSkill = studentSkillRepository.findByStudent(app.getStudent());
            List<Skill> skill = new ArrayList<>();
            for (StudentSkill s : studentSkill) {
                Skill sk = s.getSkill();
                skill.add(sk);
            }
            appr.setSkills(skill);
            appr.setStatus(app.getStatus());
            appr.setUserName(app.getStudent().getUserName());
            allApplications.add(appr);

        }
        return allApplications;

    }

    public ApiResponse updateApplicationStatus(
            Integer applicationId,
            ApplicationStatusRequest request) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null) {
            return new ApiResponse(false, "No such application exists");
        }
        Project project = app.getProject();
        if (project == null || !email.equals(project.getCreatedBy().getEmail())) {
            return new ApiResponse(false, "No such project or not authorized");
        }
        if (request == null || request.getApplicationStatus() == null) {
            return new ApiResponse(false, "Application status is required");
        }
        if (app.getStatus() != ApplicationStatus.PENDING) {
            return new ApiResponse(false, "Application status cannot be changed");
        }
        if (request.getApplicationStatus() != ApplicationStatus.ACCEPTED
                && request.getApplicationStatus() != ApplicationStatus.REJECTED) {
            return new ApiResponse(false, "invalid change of request");
        }
        app.setStatus(request.getApplicationStatus());
        applicationRepository.save(app);
        return new ApiResponse(true, "Successfully updated application status");
    }

}
