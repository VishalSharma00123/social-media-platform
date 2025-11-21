package com.upost_service.post_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private String id;
    private String userId;
    private String username;
    private String userProfilePicture;
    private String content;
    private List<String> images;        // List of image URLs
    private String video;               // URL for video
    private int likesCount;
    private int commentsCount;
    private int sharesCount;
    private boolean isLiked;
    private List<CommentResponse> recentComments;
    private LocalDateTime createdAt;
}
