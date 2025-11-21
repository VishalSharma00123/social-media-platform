package com.message_service.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage {
    private String type; // MESSAGE, TYPING, READ, STATUS
    private String conversationId;
    private MessageResponse message;
    private String userId;
    private Long timestamp;

    // For typing indicator
    private boolean isTyping;

    // For read receipts
    private String messageId;

    // For user status
    private boolean isOnline;
}