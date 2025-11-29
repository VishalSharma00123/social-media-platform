package com.notification_service.notification_service.dto;

import lombok.Data;

@Data
public class NotificationSettingsDTO {
    private boolean emailOnFollow;
    private boolean emailOnComment;
    private boolean emailOnLike;
    private boolean emailOnMention;
    private boolean emailOnMessage;
    private boolean emailOnLogin;

    private boolean pushOnFollow;
    private boolean pushOnComment;
    private boolean pushOnLike;
    private boolean pushOnMention;
    private boolean pushOnMessage;
    private boolean pushOnLogin;

    private boolean inAppOnFollow;
    private boolean inAppOnComment;
    private boolean inAppOnLike;
    private boolean inAppOnMention;
    private boolean inAppOnMessage;
    private boolean inAppOnLogin;

    private String fcmToken;
}