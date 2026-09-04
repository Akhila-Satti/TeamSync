package com.projects.teamsync.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.entity.Skill;
import com.projects.teamsync.service.SkillService;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private SkillService skillService;

    public SkillController(
            SkillService skillService) {

        this.skillService =
                skillService;
    }

    @GetMapping
    public ResponseEntity<List<Skill>>
            getAllSkills() {

        return ResponseEntity.ok(
                skillService
                        .getAllSkills());
    }
}