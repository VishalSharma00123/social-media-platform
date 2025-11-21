package com.message_service.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String id;
    private String conversationId;
    private String senderId;
    private String senderName;
    private String senderProfilePicture;
    private String receiverId;
    private String content;
    private String type;
    private String mediaUrl;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
