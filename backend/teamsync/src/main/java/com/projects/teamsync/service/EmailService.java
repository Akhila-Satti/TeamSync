package com.projects.teamsync.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private JavaMailSender javaMailSender;

    EmailService(JavaMailSender javaMailSender){
        this.javaMailSender=javaMailSender;
    }

    public void sendOtp(String email,String otp){
    SimpleMailMessage mail=new SimpleMailMessage();
    mail.setTo(email);
    mail.setSubject("TeamSync | Verify Your Email Address");
    mail.setText(
    "Hello,\n\n" +
    
    "Welcome to TeamSync! 🎉\n\n" +
    
    "Your One-Time Password (OTP) for email verification is:\n\n" +
    
    "                " + otp + "\n\n" +
    
    "This OTP is valid for 3 minutes. Please do not share it with anyone.\n\n" +
    
    "If you did not request this verification, you can safely ignore this email.\n\n" +
    
    "Best regards,\n" +
    "TeamSync Team"
);
    javaMailSender.send(mail);
}
}
