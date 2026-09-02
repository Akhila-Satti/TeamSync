package com.projects.teamsync.service;

import org.springframework.stereotype.Service;

import com.projects.teamsync.repository.SkillRepository;
import com.projects.teamsync.entity.Skill;
import java.util.List;

@Service
public class SkillService {

    private SkillRepository skillRepository;
    public SkillService(SkillRepository skillRepository){
        this.skillRepository=skillRepository;
    }

    public List<Skill> getAllSkills(){
        return skillRepository.findAll();
        
    }
    
}
