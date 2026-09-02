package com.projects.teamsync.controller;

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
import com.projects.teamsync.dto.VerifyOtpRequest;
import com.projects.teamsync.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/register")
    public ApiResponse registerRequest(@RequestBody RegisterRequest registerRequest) {
        return studentService.registerRequest(registerRequest);
    }

    @PostMapping("/verify-email")
    public ApiResponse verifyEmail(@RequestBody VerifyOtpRequest request) {
        return studentService.verifyEmail(request.getEmail(), request.getOtp());
    }

    @PostMapping("/resend-otp")
    public ApiResponse resendOtp(@RequestBody EmailRequest request) {

        return studentService.resendOtp(request.getEmail());
    }

    @PostMapping("/login")
    public LoginResponse loginRequest(@RequestBody LoginRequest request) {
        return studentService.loginRequest(request);
    }

    @PostMapping("/change-password")
    public ApiResponse changePassword(
            @RequestBody ChangePasswordRequest request) {

        return studentService.changePassword(request);
    }

    @PostMapping("/forgot-password")
    public ApiResponse forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
       return studentService.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("/verify-reset-otp")
    public ApiResponse verifyForgotPasswordOtp(@RequestBody VerifyOtpRequest request){
       return studentService.verifyForgotPasswordOtp(request.getEmail(),request.getOtp());
    }
    
    @PostMapping("/reset-password")
    public ApiResponse resetPassword(@RequestBody ResetPasswordRequest request) {
        return studentService.resetPassword(request);
    }
    
    

}
