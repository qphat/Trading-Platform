package com.koomi.treadingplatfrom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@yourcompany.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendOtp(String to, String otp) {
        String subject = "Your OTP for Two-Factor Authentication";
        String message = String.format(
                "Your OTP is: %s. This OTP will expire in 5 minutes. Do not share this OTP with anyone.",
                otp
        );
        sendSimpleMessage(to, subject, message);
    }
}