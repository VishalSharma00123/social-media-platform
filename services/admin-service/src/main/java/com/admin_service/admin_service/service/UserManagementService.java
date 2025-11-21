package com.admin_service.admin_service.service;

import com.admin_service.admin_service.client.UserServiceClient;
import com.admin_service.admin_service.dto.AdminActionRequest;
import com.admin_service.admin_service.dto.UserManagementDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

    private final UserServiceClient userServiceClient;

    public Page<UserManagementDto> getAllUsers(String search, Pageable pageable) {
        return userServiceClient.searchUsers(search, pageable);
    }

    public UserManagementDto getUserDetails(String userId) {
        return userServiceClient.getUserDetails(userId);
    }

    public void performUserAction(AdminActionRequest request, String adminId) {
        log.info("Admin {} performing action {} on user {}", adminId, request.getAction(), request.getTargetId());

        switch (request.getAction().toUpperCase()) {
            case "BAN":
                userServiceClient.banUser(request.getTargetId(), request.getReason());
                break;
            case "UNBAN":
                userServiceClient.unbanUser(request.getTargetId());
                break;
            case "DELETE":
                userServiceClient.deleteUser(request.getTargetId());
                break;
            case "VERIFY":
                userServiceClient.verifyUser(request.getTargetId());
                break;
            case "SUSPEND":
                userServiceClient.suspendUser(request.getTargetId(), request.getDuration());
                break;
            case "RESET_PASSWORD":
                userServiceClient.resetUserPassword(request.getTargetId());
                break;
            default:
                throw new RuntimeException("Unknown action: " + request.getAction());
        }

        // Log the action
        logAdminAction(adminId, request);
    }

    private void logAdminAction(String adminId, AdminActionRequest request) {
        // TODO: Implement audit logging
        log.info("Admin action logged: {} by admin {} on {}",
                request.getAction(), adminId, request.getTargetId());
    }
}

