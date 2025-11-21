package com.notification_service.notification_service.event;

import lombok.Data;

// Event DTOs
@Data
class UserEvent {
    private String type;
    private String userId;
    private String username;
    private String targetUserId;
}