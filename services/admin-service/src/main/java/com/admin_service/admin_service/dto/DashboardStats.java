package com.admin_service.admin_service.dto;

// DashboardStats.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStats {
    private UserStats userStats;
    private PostStats postStats;
    private MessageStats messageStats;
    private SystemStats systemStats;

    @Data
    @Builder
    public static class UserStats {
        private long totalUsers;
        private long activeUsers;
        private long newUsersToday;
        private long newUsersThisWeek;
        private long newUsersThisMonth;
        private Map<String, Long> usersByCountry;
    }

    @Data
    @Builder
    public static class PostStats {
        private long totalPosts;
        private long postsToday;
        private long totalLikes;
        private long totalComments;
        private double averagePostsPerUser;
        private Map<String, Long> postsByDay;
    }

    @Data
    @Builder
    public static class MessageStats {
        private long totalMessages;
        private long messagesToday;
        private long activeConversations;
    }

    @Data
    @Builder
    public static class SystemStats {
        private long totalReports;
        private long pendingReports;
        private double serverUptime;
        private Map<String, Object> performance;
    }
}

