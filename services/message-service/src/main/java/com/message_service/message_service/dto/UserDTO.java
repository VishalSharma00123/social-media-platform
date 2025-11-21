package com.message_service.message_service.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String profilePicture;
    private boolean isOnline;
}
