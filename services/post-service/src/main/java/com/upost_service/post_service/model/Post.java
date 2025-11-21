package com.upost_service.post_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String userId;
    private String username;
    private String userProfilePicture;
    private String content;
    private List<String> images = new ArrayList<>();  // URLs to uploaded images
    private String videoUrl;  // URL/path for uploaded video
    private Set<String> likes = new HashSet<>();
    private List<Comment> comments = new ArrayList<>();
    private int sharesCount = 0;
    private boolean isDeleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Embedded Comment class
    @Data
    public static class Comment {
        private String id = UUID.randomUUID().toString();
        private String userId;
        private String username;
        private String userProfilePicture;
        private String content;
        private LocalDateTime createdAt = LocalDateTime.now();
    }
}
