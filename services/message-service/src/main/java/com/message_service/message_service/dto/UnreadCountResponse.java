package com.message_service.message_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnreadCountResponse {
    private String conversationId;
    private int unreadCount;
    private int totalUnreadCount;
}
