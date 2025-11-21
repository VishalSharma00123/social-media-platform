package com.message_service.message_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    @NotNull(message = "Receiver ID is required")
    private String receiverId;

    @NotBlank(message = "Message content is required")
    private String content;

    private String type = "TEXT"; // TEXT, IMAGE, VIDEO, FILE
    private String mediaUrl;
}
