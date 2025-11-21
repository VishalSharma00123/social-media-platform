package com.admin_service.admin_service.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class AdminActionRequest {
    @NotNull
    private String targetId;

    @NotNull
    private String action; // BAN, UNBAN, DELETE, VERIFY, etc.

    private String reason;
    private int duration; // For temporary actions
}