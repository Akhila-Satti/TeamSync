package com.projects.teamsync.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.repository.EmailVerificationRepository;
import com.projects.teamsync.repository.StudentRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.enums.OtpPurpose;
import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.ChangePasswordRequest;
import com.projects.teamsync.dto.LoginRequest;
import com.projects.teamsync.dto.LoginResponse;
import com.projects.teamsync.dto.ForgotPasswordRequest;
import com.projects.teamsync.dto.RegisterRequest;
import com.projects.teamsync.dto.ResetPasswordRequest;
import com.projects.teamsync.entity.EmailVerification;

@Service
public class StudentService {

    private StudentRepository studentRepository;
    private EmailVerificationRepository emailVerificationRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private JwtService jwtService;

    @Autowired
    StudentService(StudentRepository studentRepository, EmailVerificationRepository emailVerificationRepository,
            PasswordEncoder passwordEncoder, EmailService emailService, JwtService jwtService) {
        this.studentRepository = studentRepository;
        this.emailVerificationRepository = emailVerificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    private boolean validateUserName(String userName) {

        if (userName == null) {
            return false;
        }

        // At least 4 characters and at least one alphabet
        String regex = "^(?=.*[A-Za-z]).{4,}$";

        return userName.matches(regex);
    }

    private boolean validateEmail(String email) {

        if (email == null) {
            return false;
        }

        String regex = "^[a-z]+.\\d{2}[a-z]{3}\\d{4,5}@vitapstudent\\.ac\\.in$";

        return email.matches(regex);
    }

    private boolean validatePassword(String password) {

        if (password == null) {
            return false;
        }

        String regex = "^(?=.*[a-z])" +
                "(?=.*[A-Z])" +
                "(?=.*\\d)" +
                "(?=.*[^A-Za-z0-9])" +
                ".{8,}$";

        return password.matches(regex);
    }

    private String generateOtp() {

        SecureRandom random = new SecureRandom();

        int otp = 100000 + random.nextInt(900000);

        return String.valueOf(otp);
    }

    public ApiResponse registerRequest(RegisterRequest registerRequest) {

        String userName = registerRequest.getUserName();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();

        boolean isValidUserName = validateUserName(userName);
        boolean isValidEmail = validateEmail(email);
        boolean isValidPassword = validatePassword(password);

        Student student;

        if (!isValidUserName) {
            return new ApiResponse(false,
                    "Username must be at least 4 characters and contain at least one alphabet");
        }

        if (!isValidEmail) {
            return new ApiResponse(false, "Invalid email address");
        }

        if (!isValidPassword) {
            return new ApiResponse(
                    false,
                    "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character");
        }

        boolean isEmailExists = studentRepository.existsByEmail(email);

        if (!isEmailExists) {

            boolean isUserNameExists = studentRepository.existsByUserName(userName);

            if (isUserNameExists) {
                return new ApiResponse(false, "Username already exists");
            }

            student = new Student();

            student.setUserName(userName);
            student.setEmail(email);
            student.setPassword(passwordEncoder.encode(password));
            student.setCreatedAt(LocalDateTime.now());
            student.setVerified(false);

            studentRepository.save(student);

        } else {

            student = studentRepository.findByEmail(email);

            if (student.getVerified()) {
                return new ApiResponse(false,
                        "This email is already registered and verified");
            }

            boolean isUserNameExists = studentRepository.existsByUserName(userName);

            if (isUserNameExists) {

                Student existingStudent = studentRepository.findByUserName(userName);

                if (!existingStudent.getId().equals(student.getId())) {

                    return new ApiResponse(false,
                            "Username already exists");
                }
            }

            // Update unverified registration details
            student.setUserName(userName);
            student.setPassword(passwordEncoder.encode(password));

            studentRepository.save(student);
        }

        EmailVerification record = emailVerificationRepository
                .findByStudentAndPurposeAndVerifiedFalse(student, OtpPurpose.EMAIL_VERIFICATION);

        if (record != null) {

            if (record.getExpiresAt().isAfter(LocalDateTime.now())) {
                return new ApiResponse(
                        false,
                        "An OTP is already active. Please wait until it expires.");
            }

            emailVerificationRepository.delete(record);
        }
        String otp = generateOtp();

        EmailVerification e = new EmailVerification();

        e.setStudent(student);
        e.setOtp(otp);
        e.setExpiresAt(LocalDateTime.now().plusMinutes(3));
        e.setVerified(false);
        e.setCreatedAt(LocalDateTime.now());
        e.setPurpose(OtpPurpose.EMAIL_VERIFICATION);
        emailVerificationRepository.save(e);

        emailService.sendOtp(email, otp);

        return new ApiResponse(true, "OTP sent successfully");
    }

    public ApiResponse verifyForgotPasswordOtp(String email, String otp) {
        Student student = studentRepository.findByEmail(email);

        if (student == null) {
            return new ApiResponse(false, "Email not found");
        }

        EmailVerification record = emailVerificationRepository
                .findByStudentAndPurposeAndVerifiedFalse(student, OtpPurpose.PASSWORD_RESET);

        if (record == null) {
            return new ApiResponse(false, "No active OTP found");
        }

        if (!otp.equals(record.getOtp())) {
            return new ApiResponse(false, "Incorrect OTP");
        }

        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new ApiResponse(false, "OTP has expired");
        }

        record.setVerified(true);

        emailVerificationRepository.save(record);

        return new ApiResponse(true, "OTP verified successfully");
    }

    public ApiResponse verifyEmail(String email, String otp) {

        Student student = studentRepository.findByEmail(email);

        if (student == null) {
            return new ApiResponse(false, "Email not found");
        }

        if (student.getVerified()) {
            return new ApiResponse(false, "Email is already verified");
        }

        EmailVerification record = emailVerificationRepository
                .findByStudentAndPurposeAndVerifiedFalse(student, OtpPurpose.EMAIL_VERIFICATION);

        if (record == null) {
            return new ApiResponse(false, "No active OTP found");
        }

        if (!otp.equals(record.getOtp())) {
            return new ApiResponse(false, "Incorrect OTP");
        }

        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            return new ApiResponse(false, "OTP has expired");
        }

        student.setVerified(true);
        record.setVerified(true);

        studentRepository.save(student);
        emailVerificationRepository.save(record);

        return new ApiResponse(true, "Email verified successfully");
    }

    public ApiResponse resendOtp(String email) {

        Student student = studentRepository.findByEmail(email);

        // Student does not exist
        if (student == null) {
            return new ApiResponse(false, "Email not found");
        }

        // Already verified
        if (student.getVerified()) {
            return new ApiResponse(false, "Email is already verified");
        }

        // Check for existing active/unverified OTP
        EmailVerification existingOtp = emailVerificationRepository
                .findByStudentAndPurposeAndVerifiedFalse(student, OtpPurpose.EMAIL_VERIFICATION);

        // If an OTP exists
        if (existingOtp != null) {

            // OTP is still valid
            if (existingOtp.getExpiresAt().isAfter(LocalDateTime.now())) {

                return new ApiResponse(
                        false,
                        "An OTP is already active. Please wait until it expires.");
            }

            // OTP has expired, so remove it
            emailVerificationRepository.delete(existingOtp);
        }

        // Generate new OTP
        String otp = generateOtp();

        EmailVerification newOtp = new EmailVerification();

        newOtp.setStudent(student);
        newOtp.setOtp(otp);
        newOtp.setExpiresAt(LocalDateTime.now().plusMinutes(3));
        newOtp.setVerified(false);
        newOtp.setCreatedAt(LocalDateTime.now());
        newOtp.setPurpose(OtpPurpose.EMAIL_VERIFICATION);

        // Save new OTP
        emailVerificationRepository.save(newOtp);

        // Send email
        emailService.sendOtp(email, otp);

        return new ApiResponse(true, "A new OTP has been sent successfully");
    }

    public LoginResponse loginRequest(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        Student student = studentRepository.findByEmail(email);
        if (student == null) {
            return new LoginResponse(false, "Invalid email", "");
        }
        if (!student.getVerified()) {
            return new LoginResponse(false, "email is not verified", "");
        }
        if (!passwordEncoder.matches(password, student.getPassword())) {
            return new LoginResponse(false, "Incorrect password", "");
        }
        String token = jwtService.generateToken(email);
        return new LoginResponse(true, "Logged in", token);

    }

    public ApiResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        String currentPassword = changePasswordRequest.getCurrentPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        Student student = studentRepository.findByEmail(email);

        if (student == null) {
            return new ApiResponse(false, "Student not found");
        }

        if (!passwordEncoder.matches(currentPassword, student.getPassword())) {
            return new ApiResponse(false, "Incorrect password");
        }
        boolean newPasswordValidation = validatePassword(newPassword);
        if (!newPasswordValidation) {
            return new ApiResponse(false,
                    "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character");
        }

        student.setPassword(passwordEncoder.encode(newPassword));
        studentRepository.save(student);
        return new ApiResponse(true, "Password set successfully");

    }

    public ApiResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail();
        Student student = studentRepository.findByEmail(email);
        if (student == null) {
            return new ApiResponse(false, "No user found");
        }
        if (!student.getVerified()) {
            return new ApiResponse(false, "Not a verified user");
        }
        EmailVerification existingOtp = emailVerificationRepository
                .findByStudentAndPurposeAndVerifiedFalse(student, OtpPurpose.PASSWORD_RESET);

        // If an OTP exists
        if (existingOtp != null) {

            // OTP is still valid
            if (existingOtp.getExpiresAt().isAfter(LocalDateTime.now())) {

                return new ApiResponse(
                        false,
                        "An OTP is already active. Please wait until it expires.");
            }

            // OTP has expired, so remove it
            emailVerificationRepository.delete(existingOtp);
        }

        String otp = generateOtp();

        EmailVerification newOtp = new EmailVerification();

        newOtp.setStudent(student);
        newOtp.setOtp(otp);
        newOtp.setExpiresAt(LocalDateTime.now().plusMinutes(3));
        newOtp.setVerified(false);
        newOtp.setCreatedAt(LocalDateTime.now());
        newOtp.setPurpose(OtpPurpose.PASSWORD_RESET);

        // Save new OTP
        emailVerificationRepository.save(newOtp);

        // Send email
        emailService.sendOtp(email, otp);

        return new ApiResponse(true, "A new OTP has been sent successfully");
    }

    public ApiResponse resetPassword(ResetPasswordRequest request) {

        String email = request.getEmail();
        String newPassword = request.getNewPassword();

        Student student = studentRepository.findByEmail(email);

        if (student == null) {
            return new ApiResponse(false, "Email not found");
        }

        EmailVerification record = emailVerificationRepository
                .findByStudentAndPurposeAndVerifiedTrue(
                        student,
                        OtpPurpose.PASSWORD_RESET);

        if (record == null) {
            return new ApiResponse(
                    false,
                    "Password reset OTP has not been verified");
        }

        if (!validatePassword(newPassword)) {
            return new ApiResponse(
                    false,
                    "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character");
        }

        student.setPassword(passwordEncoder.encode(newPassword));
        studentRepository.save(student);
        emailVerificationRepository.delete(record);
        return new ApiResponse(true, "Password reset successfully");
    }

}