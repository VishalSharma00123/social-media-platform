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
public class LoginNotificationRequest {
    private String userId;
    private String type;
    private String title;
    private String message;
    private Map<String, Object> metadata;
}