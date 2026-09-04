package com.projects.teamsync.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.RecommendationResponse;
import com.projects.teamsync.service.RecommendationService;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService) {

        this.recommendationService =
                recommendationService;
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<RecommendationResponse>>
            generateRecommendations(
                    @PathVariable Integer projectId) {

        List<RecommendationResponse> recommendations =
                recommendationService
                        .generateRecommendations(projectId);

        return ResponseEntity.ok(recommendations);
    }
}