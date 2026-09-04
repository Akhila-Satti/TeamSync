package com.projects.teamsync.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projects.teamsync.dto.ApiResponse;
import com.projects.teamsync.dto.ChangePasswordRequest;
import com.projects.teamsync.dto.ForgotPasswordRequest;
import com.projects.teamsync.dto.LoginRequest;
import com.projects.teamsync.dto.LoginResponse;
import com.projects.teamsync.dto.RegisterRequest;
import com.projects.teamsync.dto.ResetPasswordRequest;
import com.projects.teamsync.dto.StudentProfileResponse;
import com.projects.teamsync.dto.StudentSkillResponse;
import com.projects.teamsync.dto.UpdateStudentProfile;
import com.projects.teamsync.entity.EmailVerification;
import com.projects.teamsync.entity.Student;
import com.projects.teamsync.entity.StudentSkill;
import com.projects.teamsync.enums.Availability;
import com.projects.teamsync.enums.OtpPurpose;
import com.projects.teamsync.exception.BadRequestException;
import com.projects.teamsync.exception.ConflictException;
import com.projects.teamsync.exception.AccessDeniedException;
import com.projects.teamsync.exception.ResourceNotFoundException;
import com.projects.teamsync.exception.UnauthorizedException;
import com.projects.teamsync.repository.EmailVerificationRepository;
import com.projects.teamsync.repository.StudentRepository;
import com.projects.teamsync.repository.StudentSkillRepository;

import main.java.com.projects.teamsync.dto.StudentSearchResponse;

@Service
public class StudentService {

    private StudentRepository studentRepository;
    private EmailVerificationRepository emailVerificationRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private JwtService jwtService;
    private StudentSkillRepository studentSkillRepository;

    public StudentService(
            StudentRepository studentRepository,
            EmailVerificationRepository emailVerificationRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            JwtService jwtService,
            StudentSkillRepository studentSkillRepository) {

        this.studentRepository = studentRepository;
        this.emailVerificationRepository = emailVerificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.studentSkillRepository = studentSkillRepository;
    }

    private boolean validateUserName(String userName) {

        if (userName == null) {
            return false;
        }

        String regex = "^(?=.*[A-Za-z]).{4,}$";

        return userName.matches(regex);
    }

    private boolean validateEmail(String email) {

        if (email == null) {
            return false;
        }

        String regex =
                "^[a-z]+.\\d{2}[a-z]{3}\\d{4,5}@vitapstudent\\.ac\\.in$";

        return email.matches(regex);
    }

    private boolean validatePassword(String password) {

        if (password == null) {
            return false;
        }

        String regex =
                "^(?=.*[a-z])"
                        + "(?=.*[A-Z])"
                        + "(?=.*\\d)"
                        + "(?=.*[^A-Za-z0-9])"
                        + ".{8,}$";

        return password.matches(regex);
    }

    private String generateOtp() {

        SecureRandom random = new SecureRandom();

        int otp = 100000 + random.nextInt(900000);

        return String.valueOf(otp);
    }

    private Student getAuthenticatedStudent() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Student student = studentRepository.findByEmail(email);

        if (student == null) {
            throw new UnauthorizedException(
                    "Authenticated user not found");
        }

        return student;
    }

    public ApiResponse registerRequest(RegisterRequest registerRequest) {

        if (registerRequest == null) {
            throw new BadRequestException(
                    "Registration request is required");
        }

        String userName = registerRequest.getUserName();
        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();

        if (!validateUserName(userName)) {
            throw new BadRequestException(
                    "Username must be at least 4 characters and contain at least one alphabet");
        }

        if (!validateEmail(email)) {
            throw new BadRequestException(
                    "Invalid email address");
        }

        if (!validatePassword(password)) {
            throw new BadRequestException(
                    "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character");
        }

        Student student;

        boolean isEmailExists =
                studentRepository.existsByEmail(email);

        if (!isEmailExists) {

            if (studentRepository.existsByUserName(userName)) {
                throw new ConflictException(
                        "Username already exists");
            }

            student = new Student();

            student.setUserName(userName);
            student.setEmail(email);
            student.setPassword(
                    passwordEncoder.encode(password));
            student.setCreatedAt(LocalDateTime.now());
            student.setVerified(false);

            studentRepository.save(student);

        } else {

            student = studentRepository.findByEmail(email);

            if (student.getVerified()) {
                throw new ConflictException(
                        "This email is already registered and verified");
            }

            boolean isUserNameExists =
                    studentRepository.existsByUserName(userName);

            if (isUserNameExists) {

                Student existingStudent =
                        studentRepository.findByUserName(userName);

                if (!existingStudent.getId()
                        .equals(student.getId())) {

                    throw new ConflictException(
                            "Username already exists");
                }
            }

            student.setUserName(userName);
            student.setPassword(
                    passwordEncoder.encode(password));

            studentRepository.save(student);
        }

        EmailVerification record =
                emailVerificationRepository
                        .findByStudentAndPurposeAndVerifiedFalse(
                                student,
                                OtpPurpose.EMAIL_VERIFICATION);

        if (record != null) {

            if (record.getExpiresAt()
                    .isAfter(LocalDateTime.now())) {

                throw new ConflictException(
                        "An OTP is already active. Please wait until it expires.");
            }

            emailVerificationRepository.delete(record);
        }

        String otp = generateOtp();

        EmailVerification verification =
                new EmailVerification();

        verification.setStudent(student);
        verification.setOtp(otp);
        verification.setExpiresAt(
                LocalDateTime.now().plusMinutes(3));
        verification.setVerified(false);
        verification.setCreatedAt(LocalDateTime.now());
        verification.setPurpose(
                OtpPurpose.EMAIL_VERIFICATION);

        emailVerificationRepository.save(verification);

        emailService.sendOtp(email, otp);

        return new ApiResponse(
                true,
                "OTP sent successfully");
    }

    public ApiResponse verifyForgotPasswordOtp(
            String email,
            String otp) {

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {
            throw new ResourceNotFoundException(
                    "Email not found");
        }

        EmailVerification record =
                emailVerificationRepository
                        .findByStudentAndPurposeAndVerifiedFalse(
                                student,
                                OtpPurpose.PASSWORD_RESET);

        if (record == null) {
            throw new BadRequestException(
                    "No active OTP found");
        }

        if (!otp.equals(record.getOtp())) {
            throw new BadRequestException(
                    "Incorrect OTP");
        }

        if (record.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new BadRequestException(
                    "OTP has expired");
        }

        record.setVerified(true);

        emailVerificationRepository.save(record);

        return new ApiResponse(
                true,
                "OTP verified successfully");
    }

    public ApiResponse verifyEmail(
            String email,
            String otp) {

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {
            throw new ResourceNotFoundException(
                    "Email not found");
        }

        if (student.getVerified()) {
            throw new ConflictException(
                    "Email is already verified");
        }

        EmailVerification record =
                emailVerificationRepository
                        .findByStudentAndPurposeAndVerifiedFalse(
                                student,
                                OtpPurpose.EMAIL_VERIFICATION);

        if (record == null) {
            throw new BadRequestException(
                    "No active OTP found");
        }

        if (!otp.equals(record.getOtp())) {
            throw new BadRequestException(
                    "Incorrect OTP");
        }

        if (record.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new BadRequestException(
                    "OTP has expired");
        }

        student.setVerified(true);
        record.setVerified(true);

        studentRepository.save(student);
        emailVerificationRepository.save(record);

        return new ApiResponse(
                true,
                "Email verified successfully");
    }

    public ApiResponse resendOtp(String email) {

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {
            throw new ResourceNotFoundException(
                    "Email not found");
        }

        if (student.getVerified()) {
            throw new ConflictException(
                    "Email is already verified");
        }

        EmailVerification existingOtp =
                emailVerificationRepository
                        .findByStudentAndPurposeAndVerifiedFalse(
                                student,
                                OtpPurpose.EMAIL_VERIFICATION);

        if (existingOtp != null) {

            if (existingOtp.getExpiresAt()
                    .isAfter(LocalDateTime.now())) {

                throw new ConflictException(
                        "An OTP is already active. Please wait until it expires.");
            }

            emailVerificationRepository.delete(existingOtp);
        }

        String otp = generateOtp();

        EmailVerification newOtp =
                new EmailVerification();

        newOtp.setStudent(student);
        newOtp.setOtp(otp);
        newOtp.setExpiresAt(
                LocalDateTime.now().plusMinutes(3));
        newOtp.setVerified(false);
        newOtp.setCreatedAt(LocalDateTime.now());
        newOtp.setPurpose(
                OtpPurpose.EMAIL_VERIFICATION);

        emailVerificationRepository.save(newOtp);

        emailService.sendOtp(email, otp);

        return new ApiResponse(
                true,
                "A new OTP has been sent successfully");
    }

    public LoginResponse loginRequest(
            LoginRequest request) {

        if (request == null) {
            throw new BadRequestException(
                    "Login request is required");
        }

        String email = request.getEmail();
        String password = request.getPassword();

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {
            throw new UnauthorizedException(
                    "Invalid email or password");
        }

        if (!student.getVerified()) {
            throw new UnauthorizedException(
                    "Email is not verified");
        }

        if (!passwordEncoder.matches(
                password,
                student.getPassword())) {

            throw new UnauthorizedException(
                    "Invalid email or password");
        }

        String token =
                jwtService.generateToken(email);

        return new LoginResponse(
                true,
                "Logged in",
                token);
    }

    public ApiResponse changePassword(
            ChangePasswordRequest request) {

        if (request == null) {
            throw new BadRequestException(
                    "Password change request is required");
        }

        Student student =
                getAuthenticatedStudent();

        String currentPassword =
                request.getCurrentPassword();

        String newPassword =
                request.getNewPassword();

        if (!passwordEncoder.matches(
                currentPassword,
                student.getPassword())) {

            throw new UnauthorizedException(
                    "Incorrect current password");
        }

        if (!validatePassword(newPassword)) {
            throw new BadRequestException(
                    "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character");
        }

        student.setPassword(
                passwordEncoder.encode(newPassword));

        studentRepository.save(student);

        return new ApiResponse(
                true,
                "Password set successfully");
    }

    public ApiResponse forgotPassword(
            ForgotPasswordRequest request) {

        if (request == null ||
                request.getEmail() == null) {

            throw new BadRequestException(
                    "Email is required");
        }

        String email = request.getEmail();

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {
            throw new ResourceNotFoundException(
                    "No user found");
        }

        if (!student.getVerified()) {
            throw new UnauthorizedException(
                    "Not a verified user");
        }

        EmailVerification existingOtp =
                emailVerificationRepository
                        .findByStudentAndPurposeAndVerifiedFalse(
                                student,
                                OtpPurpose.PASSWORD_RESET);

        if (existingOtp != null) {

            if (existingOtp.getExpiresAt()
                    .isAfter(LocalDateTime.now())) {

                throw new ConflictException(
                        "An OTP is already active. Please wait until it expires.");
            }

            emailVerificationRepository.delete(existingOtp);
        }

        String otp = generateOtp();

        EmailVerification newOtp =
                new EmailVerification();

        newOtp.setStudent(student);
        newOtp.setOtp(otp);
        newOtp.setExpiresAt(
                LocalDateTime.now().plusMinutes(3));
        newOtp.setVerified(false);
        newOtp.setCreatedAt(LocalDateTime.now());
        newOtp.setPurpose(
                OtpPurpose.PASSWORD_RESET);

        emailVerificationRepository.save(newOtp);

        emailService.sendOtp(email, otp);

        return new ApiResponse(
                true,
                "OTP sent successfully");
    }

    public ApiResponse resetPassword(
            ResetPasswordRequest request) {

        if (request == null) {
            throw new BadRequestException(
                    "Reset password request is required");
        }

        String email = request.getEmail();
        String newPassword =
                request.getNewPassword();

        Student student =
                studentRepository.findByEmail(email);

        if (student == null) {
            throw new ResourceNotFoundException(
                    "Email not found");
        }

        EmailVerification record =
                emailVerificationRepository
                        .findByStudentAndPurposeAndVerifiedTrue(
                                student,
                                OtpPurpose.PASSWORD_RESET);

        if (record == null) {
            throw new BadRequestException(
                    "Password reset OTP has not been verified");
        }

        if (!validatePassword(newPassword)) {
            throw new BadRequestException(
                    "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character");
        }

        student.setPassword(
                passwordEncoder.encode(newPassword));

        studentRepository.save(student);

        emailVerificationRepository.delete(record);

        return new ApiResponse(
                true,
                "Password reset successfully");
    }

    public StudentProfileResponse viewProfile(
            Integer studentId) {

        Student student =
                studentRepository
                        .findById(studentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found"));

        List<StudentSkill> studentSkills =
                studentSkillRepository
                        .findByStudent(student);

        List<StudentSkillResponse> skills =
                new ArrayList<>();

        for (StudentSkill skill :
                studentSkills) {

            StudentSkillResponse response =
                    new StudentSkillResponse();

            response.setSkillId(
                    skill.getSkill().getId());

            response.setSkillName(
                    skill.getSkill().getName());

            response.setProficiency(
                    skill.getProficiency());

            skills.add(response);
        }

        StudentProfileResponse response =
                new StudentProfileResponse();

        response.setStudentId(student.getId());
        response.setUserName(student.getUserName());
        response.setSkills(skills);
        response.setExperience(student.getExperience());
        response.setBio(student.getBio());
        response.setAvailability(
                student.getAvailability());

        return response;
    }

    public ApiResponse updateProfile(
            Integer studentId,
            UpdateStudentProfile request) {

        if (request == null) {
            throw new BadRequestException(
                    "No field to update");
        }

        Student student =
                getAuthenticatedStudent();

        if (!student.getId()
        .equals(studentId)) {

    throw new AccessDeniedException(
            "You are not authorized to update this profile");
}

        String bio = request.getBio();
        Availability availability =
                request.getAvailability();

        Integer experience =
                request.getExperience();

        if (bio != null &&
                !bio.isBlank()) {

            student.setBio(
                    bio.strip());
        }

        if (experience != null) {

            if (experience < 0) {
                throw new BadRequestException(
                        "Experience cannot be negative");
            }

            student.setExperience(experience);
        }

        if (availability != null) {
            student.setAvailability(
                    availability);
        }

        studentRepository.save(student);

        return new ApiResponse(
                true,
                "Profile updated successfully");
    }

    public List<StudentSearchResponse> searchStudents(
        String userName) {

    if (userName == null || userName.isBlank()) {

        throw new BadRequestException(
                "Username is required");
    }

    List<Student> students =
            studentRepository
                    .findByUserNameContainingIgnoreCase(
                            userName.strip());

    List<StudentSearchResponse> response =
            new ArrayList<>();

    for (Student student : students) {

        StudentSearchResponse studentResponse =
                new StudentSearchResponse();

        studentResponse.setStudentId(
                student.getId());

        studentResponse.setUserName(
                student.getUserName());

        studentResponse.setBio(
                student.getBio());

        

        response.add(studentResponse);
    }

    return response;
}
}