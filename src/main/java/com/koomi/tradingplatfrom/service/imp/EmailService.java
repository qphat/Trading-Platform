package com.koomi.tradingplatfrom.service.imp;

import com.koomi.tradingplatfrom.exception.MailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtp(String email, String otp)  {
        try {
            String subject = "Your OTP Code";
            String text = "Your One-Time Password (OTP) is: " + otp;

            sendEmail(email, subject, text);
        } catch (MessagingException e) {
            throw new MailSendingException("Failed to prepare OTP email");
        }
    }

    public void sendVerificationEmail(String email, String otp) {
        try {
            String subject = "Please Verify Your Email";
            String text = "Your OTP is: " + otp;

            sendEmail(email, subject, text);
        } catch (MessagingException e) {
            throw new MailSendingException("Failed to send verification email");
        }
    }

    private void sendEmail(String email, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(text, true); // `true` means the email body supports HTML

        try {
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new MailSendingException("Failed to send email");
        }
    }
}
