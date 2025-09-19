package com.backend.backend.sms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@bankapp.local}")
    private String from;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    public void send(String to, String subject, String text) {
        try {
            // Skip sending if SMTP is not configured (useful for local dev)
            if (smtpUsername == null || smtpUsername.isBlank()) {
                log.info("[EMAIL SKIPPED -> {}] {} (SMTP not configured)", to, subject);
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("[EMAIL -> {}] {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
} 