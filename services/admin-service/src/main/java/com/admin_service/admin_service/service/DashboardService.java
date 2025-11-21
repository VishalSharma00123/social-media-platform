package com.admin_service.admin_service.service;

// DashboardService.java
import com.admin_service.admin_service.client.MessageServiceClient;
import com.admin_service.admin_service.client.PostServiceClient;
import com.admin_service.admin_service.client.UserServiceClient;
import com.admin_service.admin_service.dto.DashboardStats;
import com.admin_service.admin_service.dto.MessageStatsResponse;
import com.admin_service.admin_service.dto.PostStatsResponse;
import com.admin_service.admin_service.dto.UserStatsResponse;
import com.admin_service.admin_service.model.Report;
import com.admin_service.admin_service.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final UserServiceClient userServiceClient;
    private final PostServiceClient postServiceClient;
    private final MessageServiceClient messageServiceClient;
    private final ReportRepository reportRepository;

    @Cacheable(value = "dashboardStats", cacheManager = "cacheManager")
    public DashboardStats getDashboardStats() {
        try {
            // Get user statistics
            UserStatsResponse userStats = userServiceClient.getUserStatistics();
            DashboardStats.UserStats userStatsDto = DashboardStats.UserStats.builder()
                    .totalUsers(userStats.getTotalUsers())
                    .activeUsers(userStats.getActiveUsers())
                    .newUsersToday(userStats.getNewUsersToday())
                    .newUsersThisWeek(userStats.getNewUsersThisWeek())
                    .newUsersThisMonth(userStats.getNewUsersThisMonth())
                    .usersByCountry(userStats.getUsersByCountry())
                    .build();

            // Get post statistics
            PostStatsResponse postStats = postServiceClient.getPostStatistics();
            DashboardStats.PostStats postStatsDto = DashboardStats.PostStats.builder()
                    .totalPosts(postStats.getTotalPosts())
                    .postsToday(postStats.getPostsToday())
                    .totalLikes(postStats.getTotalLikes())
                    .totalComments(postStats.getTotalComments())
                    .averagePostsPerUser(postStats.getAveragePostsPerUser())
                    .postsByDay(postStats.getPostsByDay())
                    .build();

            // Get message statistics
            MessageStatsResponse messageStats = messageServiceClient.getMessageStatistics();
            DashboardStats.MessageStats messageStatsDto = DashboardStats.MessageStats.builder()
                    .totalMessages(messageStats.getTotalMessages())
                    .messagesToday(messageStats.getMessagesToday())
                    .activeConversations(messageStats.getActiveConversations())
                    .build();

            // Get system statistics
            long totalReports = reportRepository.count();
            long pendingReports = reportRepository.countByStatus(Report.ReportStatus.PENDING);

            DashboardStats.SystemStats systemStats = DashboardStats.SystemStats.builder()
                    .totalReports(totalReports)
                    .pendingReports(pendingReports)
                    .serverUptime(getServerUptime())
                    .performance(getPerformanceMetrics())
                    .build();

            return DashboardStats.builder()
                    .userStats(userStatsDto)
                    .postStats(postStatsDto)
                    .messageStats(messageStatsDto)
                    .systemStats(systemStats)
                    .build();

        } catch (Exception e) {
            log.error("Error getting dashboard stats: ", e);
            throw new RuntimeException("Failed to fetch dashboard statistics");
        }
    }

    private double getServerUptime() {
        // Calculate server uptime
        return 99.9; // Placeholder
    }

    private Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("cpuUsage", 45.5);
        metrics.put("memoryUsage", 60.2);
        metrics.put("diskUsage", 35.8);
        return metrics;
    }
}


