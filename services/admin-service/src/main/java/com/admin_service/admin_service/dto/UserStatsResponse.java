package com.admin_service.admin_service.dto;

import lombok.Data;

import java.util.Map;

// Response DTOs
@Data
public class UserStatsResponse {
    private long totalUsers;
    private long activeUsers;
    private long newUsersToday;
    private long newUsersThisWeek;
    private long newUsersThisMonth;
    private Map<String, Long> usersByCountry;
}

