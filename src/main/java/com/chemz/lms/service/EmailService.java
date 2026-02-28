package com.chemz.lms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a verification email containing a code to the specified recipient.
     * This method runs asynchronously to prevent blocking HTTP requests.
     *
     * @param to   recipient email address
     * @param code verification code to send
     */
    @Async
    public void sendVerificationEmail(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Your Verification Code");
            helper.setText(
                    "<h3>Your verification code:</h3>" +
                            "<h2 style='color:#4CAF50'>" + code + "</h2>" +
                            "<p>This code will expire in 10 minutes.</p>",
                    true // true indicates HTML content
            );

            mailSender.send(message);
            System.out.println("Verification email sent to: " + to);
        } catch (MessagingException e) {
            System.err.println("Failed to send verification email to " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
