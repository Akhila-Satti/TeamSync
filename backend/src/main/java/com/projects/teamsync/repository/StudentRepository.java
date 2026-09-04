package com.projects.teamsync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.teamsync.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    
     boolean existsByEmail(String email);
     boolean existsByUserName(String userName);

     Student findByEmail(String email);
     Student findByUserName(String userName);
     List<Student> findByUserNameContainingIgnoreCase(String userName);
}
