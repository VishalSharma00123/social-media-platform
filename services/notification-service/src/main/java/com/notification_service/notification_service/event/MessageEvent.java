package com.notification_service.notification_service.event;

import lombok.Data;

@Data
public class MessageEvent {
    private String type;
    private String conversationId;
    private String senderId;
    private String senderName;
    private String receiverId;
}

