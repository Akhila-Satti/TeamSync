package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Application;
import com.projects.teamsync.entity.Project;
import com.projects.teamsync.entity.Student;
import java.util.List;


public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    boolean existsByStudentAndProject(Student student,Project project);
    List<Application> findByProject(Project project);
    List<Application> findByStudent(Student student);
}
