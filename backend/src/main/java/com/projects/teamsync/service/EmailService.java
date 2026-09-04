package com.projects.teamsync.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import com.projects.teamsync.exception.EmailSendingException;

@Service
public class EmailService {

        private JavaMailSender javaMailSender;

        public EmailService(
                        JavaMailSender javaMailSender) {

                this.javaMailSender = javaMailSender;
        }

        public void sendOtp(
                        String email,
                        String otp) {

                try {

                        SimpleMailMessage mail = new SimpleMailMessage();

                        mail.setTo(email);

                        mail.setSubject(
                                        "TeamSync | Verify Your Email Address");

                        mail.setText(
                                        "Hello,\n\n"
                                                        + "Welcome to TeamSync! 🎉\n\n"
                                                        + "Your One-Time Password (OTP) is:\n\n"
                                                        + otp
                                                        + "\n\n"
                                                        + "This OTP is valid for 3 minutes.\n\n"
                                                        + "Please do not share it with anyone.\n\n"
                                                        + "Best regards,\n"
                                                        + "TeamSync Team");

                        javaMailSender.send(mail);

                } catch (Exception e) {

                        throw new EmailSendingException(
                                        "Failed to send OTP email");
                }
        }
}