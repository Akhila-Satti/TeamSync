package com.projects.teamsync.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.ProjectMember;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.enums.ProjectMemberStatus;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Integer> {
        boolean existsByProjectAndStudent(Project project, Student student);

        List<ProjectMember> findByProject(Project project);

        List<ProjectMember> findByProjectAndStatus(
                        Project project,
                        ProjectMemberStatus status);

        Optional<ProjectMember> findByProjectAndStudent(
                        Project project,
                        Student student);

        List<ProjectMember> findByStudentAndStatus(
                        Student student,
                        ProjectMemberStatus status);

        long countByProjectAndStatus(
                        Project project,
                        ProjectMemberStatus status);

        boolean existsByProjectAndStudentAndStatus(Project project, Student student, ProjectMemberStatus status);
}
