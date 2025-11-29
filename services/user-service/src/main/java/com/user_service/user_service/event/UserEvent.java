package com.user_service.user_service.event;

import lombok.Data;

// Event DTOs
@Data
public class UserEvent {
    private String type;
    private String userId;
    private String username;
    private String targetUserId;
}