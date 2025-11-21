package com.message_service.message_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TypingIndicatorRequest {
    @NotNull
    private String conversationId;

    @NotNull
    private Boolean isTyping;
}