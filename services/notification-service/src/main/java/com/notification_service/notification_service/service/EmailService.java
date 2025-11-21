package com.notification_service.notification_service.service;

import com.notification_service.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:Social Media}")
    private String appName;

    @Async
    public void sendNotificationEmail(Notification notification, String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(notification.getTitle() + " - " + appName);
            message.setText(buildEmailBody(notification));
            mailSender.send(message);
            log.info("Email sent to {} for notification {}", toEmail, notification.getId());
        } catch  (Exception e) {
            log.error("Failed to send email for notification {}: {}", notification.getId(), e.getMessage());
        }
    }

    private String buildEmailBody(Notification notification) {
        StringBuilder body = new StringBuilder();
        body.append("Hi,\n\n");
        body.append(notification.getMessage()).append("\n\n");

        if (notification.getSenderName() != null) {
            body.append("From: ").append(notification.getSenderName()).append("\n");
        }

        body.append("\nBest regards,\n");
        body.append(appName).append(" Team");

        return body.toString();
    }
}