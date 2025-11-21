package com.notification_service.notification_service.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import com.notification_service.notification_service.model.Notification;

@Service
@Slf4j
public class PushNotificationService {

    @Async
    public void sendPushNotification(Notification notification, String fcmToken) {
        // Check if Firebase is initialized
        if (FirebaseApp.getApps().isEmpty()) {
            log.debug("Firebase not initialized - skipping mobile push notification for: {}",
                    notification.getId());
            return;  // Skip silently
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(notification.getTitle())
                            .setBody(notification.getMessage())
                            .build())
                    .putAllData(buildDataPayload(notification))
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("📱 Push notification sent successfully: {}", response);
        } catch (Exception e) {
            log.error("❌ Failed to send push notification: {}", e.getMessage());
        }
    }

    private Map<String, String> buildDataPayload(Notification notification) {
        Map<String, String> data = new HashMap<>();
        data.put("notificationId", notification.getId());
        data.put("type", notification.getType().toString());
        data.put("targetId", notification.getTargetId() != null ? notification.getTargetId() : "");
        data.put("senderId", notification.getSenderId() != null ? notification.getSenderId() : "");
        return data;
    }
}
