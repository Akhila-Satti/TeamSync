package com.projects.teamsync.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.CreateReferralRequest;
import com.projects.teamsync.dto.ReferralResponse;
import com.projects.teamsync.dto.ReferralStatusRequest;
import com.projects.teamsync.service.ReferralService;

@RestController
@RequestMapping("/api/referrals")
public class ReferralController {

    private ReferralService referralService;

    public ReferralController(
            ReferralService referralService) {

        this.referralService =
                referralService;
    }

    @PostMapping("/{projectId}")
    public ApiResponse createReferral(
            @PathVariable Integer projectId,
            @RequestBody CreateReferralRequest request) {

        return referralService
                .createReferral(
                        projectId,
                        request);
    }

    @GetMapping("/{projectId}")
    public List<ReferralResponse> getProjectReferrals(
            @PathVariable Integer projectId) {

        return referralService
                .getProjectReferrals(projectId);
    }

    @PatchMapping("/{referralId}")
    public ApiResponse updateReferralStatus(
            @PathVariable Integer referralId,
            @RequestBody ReferralStatusRequest request) {

        return referralService
                .updateReferralStatus(
                        referralId,
                        request);
    }
}