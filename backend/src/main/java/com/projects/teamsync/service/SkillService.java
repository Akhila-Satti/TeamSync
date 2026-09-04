package com.projects.teamsync.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.repository.SkillRepository;

@Service
public class SkillService {

    private SkillRepository skillRepository;

    public SkillService(
            SkillRepository skillRepository) {

        this.skillRepository =
                skillRepository;
    }

    public List<Skill> getAllSkills() {

        return skillRepository.findAll();
    }
}