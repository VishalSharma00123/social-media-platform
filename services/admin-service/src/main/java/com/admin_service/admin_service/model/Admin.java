package com.admin_service.admin_service.model;

// Admin.java

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Document(collection = "admins")
public class Admin {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String fullName;

    private AdminRole role;
    private Set<String> permissions;

    private boolean isActive = true;
    private LocalDateTime lastLogin;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum AdminRole {
        SUPER_ADMIN,
        ADMIN,
        MODERATOR,
        SUPPORT
    }
}
