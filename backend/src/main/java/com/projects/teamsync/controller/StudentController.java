package com.projects.teamsync.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.ChangePasswordRequest;
import com.projects.teamsync.dto.EmailRequest;
import com.projects.teamsync.dto.ForgotPasswordRequest;
import com.projects.teamsync.dto.LoginRequest;
import com.projects.teamsync.dto.LoginResponse;
import com.projects.teamsync.dto.RegisterRequest;
import com.projects.teamsync.dto.ResetPasswordRequest;
import com.projects.teamsync.dto.StudentProfileResponse;
import com.projects.teamsync.dto.UpdateStudentProfile;
import com.projects.teamsync.dto.VerifyOtpRequest;

import com.projects.teamsync.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private StudentService studentService;

    public StudentController(
            StudentService studentService) {

        this.studentService =
                studentService;
    }


    // ================= REGISTER =================

    @PostMapping("/register")
    public ResponseEntity<ApiResponse>
            registerRequest(
                    @RequestBody
                    RegisterRequest request) {

        ApiResponse response =
                studentService
                        .registerRequest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ================= VERIFY EMAIL =================

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse>
            verifyEmail(
                    @RequestBody
                    VerifyOtpRequest request) {

        return ResponseEntity.ok(

                studentService
                        .verifyEmail(
                                request.getEmail(),
                                request.getOtp())
        );
    }


    // ================= RESEND OTP =================

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse>
            resendOtp(
                    @RequestBody
                    EmailRequest request) {

        return ResponseEntity.ok(

                studentService
                        .resendOtp(
                                request.getEmail())
        );
    }


    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>
            loginRequest(
                    @RequestBody
                    LoginRequest request) {

        return ResponseEntity.ok(

                studentService
                        .loginRequest(request)
        );
    }


    // ================= CHANGE PASSWORD =================

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse>
            changePassword(
                    @RequestBody
                    ChangePasswordRequest request) {

        return ResponseEntity.ok(

                studentService
                        .changePassword(request)
        );
    }


    // ================= FORGOT PASSWORD =================

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse>
            forgotPassword(
                    @RequestBody
                    ForgotPasswordRequest request) {

        return ResponseEntity.ok(

                studentService
                        .forgotPassword(request)
        );
    }


    // ================= VERIFY RESET OTP =================

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ApiResponse>
            verifyForgotPasswordOtp(
                    @RequestBody
                    VerifyOtpRequest request) {

        return ResponseEntity.ok(

                studentService
                        .verifyForgotPasswordOtp(
                                request.getEmail(),
                                request.getOtp())
        );
    }


    // ================= RESET PASSWORD =================

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse>
            resetPassword(
                    @RequestBody
                    ResetPasswordRequest request) {

        return ResponseEntity.ok(

                studentService
                        .resetPassword(request)
        );
    }


    // ================= VIEW PROFILE =================

    @GetMapping("/profile/{studentId}")
    public ResponseEntity<StudentProfileResponse>
            viewProfile(
                    @PathVariable
                    Integer studentId) {

        return ResponseEntity.ok(

                studentService
                        .viewProfile(studentId)
        );
    }


    // ================= UPDATE PROFILE =================

    @PatchMapping("/profile/{studentId}")
    public ResponseEntity<ApiResponse>
            updateProfile(

                    @PathVariable
                    Integer studentId,

                    @RequestBody
                    UpdateStudentProfile request) {

        return ResponseEntity.ok(

                studentService
                        .updateProfile(
                                studentId,
                                request)
        );
    }
}