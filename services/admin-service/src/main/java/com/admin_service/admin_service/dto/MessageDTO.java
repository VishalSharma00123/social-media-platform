package com.admin_service.admin_service.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private String id;
    private String senderId;
    private String content;
    private LocalDateTime createdAt;
}

