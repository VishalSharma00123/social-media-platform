package com.notification_service.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    private String userId;
    private String senderId;
    private String type;
    private String title;
    private String message;
    private String targetId;
    private Map<String, Object> metadata;
}
