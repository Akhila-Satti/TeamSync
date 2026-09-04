package com.projects.teamsync.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.InvitationResponse;
import com.projects.teamsync.dto.InvitationStatusRequest;

import com.projects.teamsync.service.InvitationService;


@RestController
@RequestMapping("/api/invitations")
public class InvitationController {


    private InvitationService invitationService;


    public InvitationController(
            InvitationService invitationService) {

        this.invitationService =
                invitationService;
    }


    // ================= MY INVITATIONS =================

    @GetMapping("/my-invitations")
    public ResponseEntity<List<InvitationResponse>>
            getMyInvitations() {


        return ResponseEntity.ok(

                invitationService
                        .getMyInvitations()
        );
    }


    // ================= UPDATE INVITATION STATUS =================

    @PatchMapping("/{invitationId}/status")
    public ResponseEntity<ApiResponse>
            updateInvitationStatus(

                    @PathVariable
                    Integer invitationId,

                    @RequestBody
                    InvitationStatusRequest request) {


        return ResponseEntity.ok(

                invitationService
                        .updateInvitationStatus(

                                invitationId,

                                request)
        );
    }
}