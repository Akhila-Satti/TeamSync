package com.projects.teamsync.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.entity.StudentSkill;
import java.util.List;


public interface StudentSkillRepository extends JpaRepository<StudentSkill, Integer>{
    boolean existsByStudentAndSkill(Student student,Skill skill);

    List<StudentSkill> findByStudent(Student student);
    List<StudentSkill> findBySkill(Skill skill);
}
