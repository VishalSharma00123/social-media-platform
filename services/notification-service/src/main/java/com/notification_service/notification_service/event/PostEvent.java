package com.notification_service.notification_service.event;


import lombok.Data;

@Data
class PostEvent {
    private String type;
    private String postId;
    private String postOwnerId;
    private String userId;
    private String username;
    private String comment;
}