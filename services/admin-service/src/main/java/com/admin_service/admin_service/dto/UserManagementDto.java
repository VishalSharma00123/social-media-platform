package com.admin_service.admin_service.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserManagementDto {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private boolean isActive;
    private boolean isVerified;
    private int followersCount;
    private int followingCount;
    private int postsCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastSeen;
}
