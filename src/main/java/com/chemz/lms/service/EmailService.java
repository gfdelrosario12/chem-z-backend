package com.chemz.lms.service;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Send email
    public void sendVerificationEmail(String to, String code) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Your Verification Code");
        helper.setText(
                "<h3>Your verification code:</h3><h2 style='color:#4CAF50'>" + code + "</h2>" +
                        "<p>This code will expire in 10 minutes.</p>",
                true
        );

        mailSender.send(message);
    }
}
