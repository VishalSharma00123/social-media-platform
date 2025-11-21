package com.admin_service.admin_service.dto;


import lombok.Data;

import java.util.Map;

@Data
public class PostStatsResponse {
    private long totalPosts;
    private long postsToday;
    private long totalLikes;
    private long totalComments;
    private double averagePostsPerUser;
    private Map<String, Long> postsByDay;
}