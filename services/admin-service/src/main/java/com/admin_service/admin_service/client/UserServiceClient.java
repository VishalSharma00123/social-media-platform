package com.admin_service.admin_service.client;

// UserServiceClient.java

import com.admin_service.admin_service.dto.UserManagementDto;
import com.admin_service.admin_service.dto.UserStatsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/admin/statistics")
    UserStatsResponse getUserStatistics();

    @GetMapping("/api/users/admin/search")
    Page<UserManagementDto> searchUsers(@RequestParam String search, @RequestParam Pageable pageable);

    @GetMapping("/api/users/admin/{userId}")
    UserManagementDto getUserDetails(@PathVariable String userId);

    @PostMapping("/api/users/admin/{userId}/ban")
    void banUser(@PathVariable String userId, @RequestParam String reason);

    @PostMapping("/api/users/admin/{userId}/unban")
    void unbanUser(@PathVariable String userId);

    @DeleteMapping("/api/users/admin/{userId}")
    void deleteUser(@PathVariable String userId);

    @PostMapping("/api/users/admin/{userId}/verify")
    void verifyUser(@PathVariable String userId);

    @PostMapping("/api/users/admin/{userId}/suspend")
    void suspendUser(@PathVariable String userId, @RequestParam int duration);

    @PostMapping("/api/users/admin/{userId}/reset-password")
    void resetUserPassword(@PathVariable String userId);
}

