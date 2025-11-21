package com.admin_service.admin_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReportedPostDTO {
    private String postId;
    private String content;
    private String userId;
    private String username;
    private int reportCount;
    private List<String> reportReasons;
    private LocalDateTime createdAt;
}