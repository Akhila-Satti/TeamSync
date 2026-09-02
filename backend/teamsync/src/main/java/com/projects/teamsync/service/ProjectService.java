package com.projects.teamsync.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.CreateProjectRequest;
import com.projects.teamsync.dto.ProjectResponse;
import com.projects.teamsync.dto.ProjectSkillRequest;
import com.projects.teamsync.dto.ProjectSkillSet;
import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectSkill;
import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.enums.ProjectStatus;
import com.projects.teamsync.repository.ProjectRepository;
import com.projects.teamsync.repository.ProjectSkillRepository;
import com.projects.teamsync.repository.SkillRepository;
import com.projects.teamsync.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
// roll back as 2 skills get added nad third one gives error so it should not
// add the whole set
@Transactional
public class ProjectService {

    private ProjectRepository projectRepository;
    private StudentRepository studentRepository;
    private SkillRepository skillRepository;
    private ProjectSkillRepository projectSkillRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, StudentRepository studentRepository,
            SkillRepository skillRepository,
            ProjectSkillRepository projectSkillRepository) {
        this.projectRepository = projectRepository;
        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
        this.projectSkillRepository = projectSkillRepository;
    }

    public ApiResponse createProject(CreateProjectRequest createProjectRequest) {
        String name = createProjectRequest.getName();
        String description = createProjectRequest.getDescription();
        String domain = createProjectRequest.getDomain();
        Integer desiredTeamSize = createProjectRequest.getDesiredTeamSize();

        if (name == null || name.isBlank() || name.length() < 2) {
            return new ApiResponse(false, "Name of project must be greater than 2 Characters");
        }

        if (domain == null || domain.isBlank()) {
            return new ApiResponse(false, "Must select the domain");
        }

        if (desiredTeamSize == null || desiredTeamSize <= 0) {
            return new ApiResponse(false, "Team size must be greater than or equal to 1");
        }

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        if (email == null) {
            return new ApiResponse(false, "User is not authenticated");
        }
        Student student = studentRepository.findByEmail(email);
        if (student == null) {
            return new ApiResponse(false, "no user found");
        }

        List<ProjectSkillRequest> projectSkills = createProjectRequest.getProjectSkills();
        if (projectSkills == null||projectSkills.isEmpty()) {
            return new ApiResponse(false, "Invalid skills set");
        }
        HashSet<Integer> userSkillIds=new HashSet<>();
        List<ProjectSkillSet> validateskills=new ArrayList<>();
        for (ProjectSkillRequest projectSkill : projectSkills) {
            Integer skillId = projectSkill.getSkillId();
            Integer importance = projectSkill.getImportance();
            if (skillId == null) {
                return new ApiResponse(false, "Invalid skillid");
            }
            Skill skill = skillRepository.findById(skillId).orElse(null);
            if (skill == null) {
                return new ApiResponse(false, "no such skill");
            }
            if (importance == null || importance < 1 || importance > 5) {
                return new ApiResponse(false, "Invalid importance range");
            }
            if(!userSkillIds.contains(skillId)){
                userSkillIds.add(skillId);
            ProjectSkillSet ps=new ProjectSkillSet();
            ps.setSkill(skill);
            ps.setImportance(importance);
            validateskills.add(ps);
            }


        }

        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setDomain(domain);
        project.setDesiredTeamSize(desiredTeamSize);
        project.setCreatedBy(student);
        project.setStatus(ProjectStatus.OPEN);
        project.setCreatedAt(LocalDateTime.now());
        Project p = projectRepository.save(project);

        for (ProjectSkillSet projectSkill : validateskills) {
            

            ProjectSkill addProjectSkill = new ProjectSkill();
            addProjectSkill.setProject(p);
            addProjectSkill.setSkill(projectSkill.getSkill());
            addProjectSkill.setImportance(projectSkill.getImportance());

            projectSkillRepository.save(addProjectSkill);
        }

        return new ApiResponse(true, "Created project successfully");
    }

    public List<ProjectResponse> getAllProjects(){
         List<Project> activeprojects=projectRepository.findByStatus(ProjectStatus.OPEN);
         List<ProjectResponse> allprojects=new ArrayList<>();
         for(Project project:activeprojects){
            ProjectResponse projectResponse=new ProjectResponse();
            projectResponse.setCreatedAt(project.getCreatedAt());
            projectResponse.setCreatorId(project.getCreatedBy().getId());
            projectResponse.setProjectId(project.getId());
            projectResponse.setName(project.getName());
            projectResponse.setDomain(project.getDomain());
            projectResponse.setDescription(project.getDescription());
            projectResponse.setDesiredTeamSize(project.getDesiredTeamSize());
            projectResponse.setStatus(project.getStatus());

            allprojects.add(projectResponse);

         }
         return allprojects;
    }

    public List<ProjectResponse> getMyProjects(){
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        Student student=studentRepository.findByEmail(email);
        List<Project> activeprojects=projectRepository.findByCreatedBy(student);
         List<ProjectResponse> allprojects=new ArrayList<>();
         for(Project project:activeprojects){
            ProjectResponse projectResponse=new ProjectResponse();
            projectResponse.setCreatedAt(project.getCreatedAt());
            projectResponse.setCreatorId(project.getCreatedBy().getId());
            projectResponse.setProjectId(project.getId());
            projectResponse.setName(project.getName());
            projectResponse.setDomain(project.getDomain());
            projectResponse.setDescription(project.getDescription());
            projectResponse.setDesiredTeamSize(project.getDesiredTeamSize());
            projectResponse.setStatus(project.getStatus());

            allprojects.add(projectResponse);

         }
         return allprojects;
    }

    public ApiResponse closeProject(Integer projectId) {
        String email=SecurityContextHolder.getContext().getAuthentication().getName();
        Student student=studentRepository.findByEmail(email);
        Project project=projectRepository.findById(projectId).orElse(null);
        if(student==null){
            return new ApiResponse(false, "no user found");
        }
        if(project==null){
            return new ApiResponse(false, "no project found");

        }
        if (!project.getCreatedBy().getId().equals(student.getId())){
            return new ApiResponse(false, "Unauthorised access");
        }
        if(project.getStatus() == ProjectStatus.CLOSED){
            return new ApiResponse(true, "Already closed project");
        }
        project.setStatus(ProjectStatus.CLOSED);
        project.setClosedAt(LocalDateTime.now());
        projectRepository.save(project);
        return new ApiResponse(true,"successfully closed project");
    }
}
