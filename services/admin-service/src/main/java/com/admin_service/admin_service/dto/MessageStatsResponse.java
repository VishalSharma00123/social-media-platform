package com.admin_service.admin_service.dto;

import lombok.Data;

@Data
public class MessageStatsResponse {
    private long totalMessages;
    private long messagesToday;
    private long activeConversations;
}