package com.notification_service.notification_service.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String profilePicture;
    private String fullName;
}