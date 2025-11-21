package com.notification_service.notification_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "notification_settings")
public class NotificationSettings {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    // Email notifications
    private boolean emailOnFollow = true;
    private boolean emailOnComment = true;
    private boolean emailOnLike = false;
    private boolean emailOnMention = true;
    private boolean emailOnMessage = true;

    // Push notifications
    private boolean pushOnFollow = true;
    private boolean pushOnComment = true;
    private boolean pushOnLike = true;
    private boolean pushOnMention = true;
    private boolean pushOnMessage = true;

    // In-app notifications
    private boolean inAppOnFollow = true;
    private boolean inAppOnComment = true;
    private boolean inAppOnLike = true;
    private boolean inAppOnMention = true;
    private boolean inAppOnMessage = true;

    private String fcmToken; // Firebase Cloud Messaging token

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

