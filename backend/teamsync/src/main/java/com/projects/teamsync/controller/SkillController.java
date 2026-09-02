package com.projects.teamsync.controller;

import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.service.SkillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import com.projects.teamsync.entity.Skill;
@RestController
@RequestMapping("api/skills")
public class SkillController {

    private SkillService skillService;
    public SkillController(SkillService skillService){
        this.skillService=skillService;
    }

    @GetMapping
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }
    
    
}
