package com.admin_service.admin_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// DTOs for MessageServiceClient
@Data
public class ConversationDetailsDTO {
    private String id;
    private List<String> participants;
    private List<MessageDTO> messages;
    private LocalDateTime createdAt;
}
