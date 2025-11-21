package com.user_service.user_service.dto;

import java.time.LocalDateTime;

public class UserProfileResponse {
    private String id;
    private String username;
    private String fullName;
    private String bio;
    private String profilePicture;
    private String coverPicture;
    private int followersCount;
    private int followingCount;
    private boolean isVerified;
    private LocalDateTime createdAt;

    // Private constructor for builder
    private UserProfileResponse(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.fullName = builder.fullName;
        this.bio = builder.bio;
        this.profilePicture = builder.profilePicture;
        this.coverPicture = builder.coverPicture;
        this.followersCount = builder.followersCount;
        this.followingCount = builder.followingCount;
        this.isVerified = builder.isVerified;
        this.createdAt = builder.createdAt;
    }

    // Default constructor
    public UserProfileResponse() {
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getCoverPicture() { return coverPicture; }
    public void setCoverPicture(String coverPicture) { this.coverPicture = coverPicture; }

    public int getFollowersCount() { return followersCount; }
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }

    public int getFollowingCount() { return followingCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Manual Builder
    public static class Builder {
        private String id;
        private String username;
        private String fullName;
        private String bio;
        private String profilePicture;
        private String coverPicture;
        private int followersCount;
        private int followingCount;
        private boolean isVerified;
        private LocalDateTime createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder profilePicture(String profilePicture) { this.profilePicture = profilePicture; return this; }
        public Builder coverPicture(String coverPicture) { this.coverPicture = coverPicture; return this; }
        public Builder followersCount(int followersCount) { this.followersCount = followersCount; return this; }
        public Builder followingCount(int followingCount) { this.followingCount = followingCount; return this; }
        public Builder isVerified(boolean isVerified) { this.isVerified = isVerified; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public UserProfileResponse build() {
            return new UserProfileResponse(this);
        }
    }
}
