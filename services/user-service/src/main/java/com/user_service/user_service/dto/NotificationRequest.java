package com.user_service.user_service.dto;

import lombok.Data;
import java.util.Map;

@Data
public class NotificationRequest {
    private String userId;  // recipient user ID
    private String senderId;  // sender/follower user ID
    private String type;  // e.g. "FOLLOW"
    private String title;
    private String message;
    private String targetId;  // e.g. linked user or post ID
    private Map<String, Object> metadata;
}
