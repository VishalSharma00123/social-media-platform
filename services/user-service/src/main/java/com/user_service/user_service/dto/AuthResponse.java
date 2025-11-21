package com.user_service.user_service.dto;

public class AuthResponse {
    private String token;
    private String userId;
    private String username;
    private String email;
    private String profilePicture;

    // Private constructor for builder
    private AuthResponse(Builder builder) {
        this.token = builder.token;
        this.userId = builder.userId;
        this.username = builder.username;
        this.email = builder.email;
        this.profilePicture = builder.profilePicture;
    }

    // Default constructor
    public AuthResponse() {
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    // Manual Builder
    public static class Builder {
        private String token;
        private String userId;
        private String username;
        private String email;
        private String profilePicture;

        public Builder token(String token) { this.token = token; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder profilePicture(String profilePicture) { this.profilePicture = profilePicture; return this; }

        public AuthResponse build() {
            return new AuthResponse(this);
        }
    }
}
