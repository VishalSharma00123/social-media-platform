package com.upost_service.post_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private String id;
    private String userId;
    private String senderId;
    private String senderName;
    private String senderProfilePicture;
    private String type;
    private String title;
    private String message;
    private String targetId;
    private Map<String, Object> metadata;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}

