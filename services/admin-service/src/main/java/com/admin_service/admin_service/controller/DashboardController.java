package com.admin_service.admin_service.controller;

// DashboardController.java

import com.admin_service.admin_service.dto.DashboardStats;
import com.admin_service.admin_service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/stats/realtime")
    public ResponseEntity<Map<String, Object>> getRealtimeStats() {
        // Return real-time stats for WebSocket updates
        Map<String, Object> realtimeStats = new HashMap<>();
        realtimeStats.put("onlineUsers", 1234);
        realtimeStats.put("activeNow", 567);
        return ResponseEntity.ok(realtimeStats);
    }
}

// UserManagementController.java
