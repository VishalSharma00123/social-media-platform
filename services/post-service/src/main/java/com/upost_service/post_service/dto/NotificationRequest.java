package com.upost_service.post_service.dto;

import lombok.Data;
import java.util.Map;

@Data
public class NotificationRequest {
    private String userId;  // recipient user ID
    private String senderId;  // sender/follower user ID
    private String type;
    private String title;
    private String message;
    private String targetId;
    private Map<String, Object> metadata;
}

