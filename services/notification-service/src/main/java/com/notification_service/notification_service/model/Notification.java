package com.notification_service.notification_service.model;

// Notification.java
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    @Indexed
    private String userId;

    private String senderId;
    private String senderName;
    private String senderProfilePicture;

    private NotificationType type;
    private String title;
    private String message;
    private String targetId; // postId, userId, etc.
    private Map<String, Object> metadata;

    private boolean isRead = false;
    private boolean isPushSent = false;
    private boolean isEmailSent = false;

    @Indexed
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public enum NotificationType {
        FOLLOW,
        LOGIN,
        REGISTRATION,
        PROFILE_UPDATE,
        PICTURE_UPLOAD,
        LIKE_POST,
        COMMENT,
        MESSAGE,
    }
}
