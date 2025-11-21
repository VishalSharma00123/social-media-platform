package com.notification_service.notification_service.controller;

import com.notification_service.notification_service.dto.NotificationRequest;
import com.notification_service.notification_service.dto.NotificationResponse;
import com.notification_service.notification_service.dto.NotificationSettingsDTO;
import com.notification_service.notification_service.model.NotificationSettings;
import com.notification_service.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Create notification - For internal service calls only
     * In production, this should be moved to /internal/notifications
     * and called only by other microservices
     */
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    /**
     * Get user's notifications - Requires authentication
     * userId is extracted from JWT token (verified by API Gateway)
     */
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Add validation
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().build();  // Return proper error response
        }

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(notificationService.getUserNotifications(userId, unreadOnly, pageable));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String notificationId,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        notificationService.markAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/settings")
    public ResponseEntity<NotificationSettings> getSettings(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {

        return ResponseEntity.ok(notificationService.getNotificationSettings(userId));
    }

    @PutMapping("/settings")
    public ResponseEntity<NotificationSettings> updateSettings(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody NotificationSettingsDTO settingsDTO) {

        return ResponseEntity.ok(notificationService.updateNotificationSettings(userId, settingsDTO));
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<Void> updateFcmToken(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestBody Map<String, String> request) {

        String fcmToken = request.get("fcmToken");
        NotificationSettings settings = notificationService.getNotificationSettings(userId);

        NotificationSettingsDTO dto = new NotificationSettingsDTO();
        // Copy existing settings
        dto.setEmailOnFollow(settings.isEmailOnFollow());
        dto.setEmailOnComment(settings.isEmailOnComment());
        dto.setEmailOnLike(settings.isEmailOnLike());
        dto.setEmailOnMention(settings.isEmailOnMention());
        dto.setEmailOnMessage(settings.isEmailOnMessage());
        dto.setPushOnFollow(settings.isPushOnFollow());
        dto.setPushOnComment(settings.isPushOnComment());
        dto.setPushOnLike(settings.isPushOnLike());
        dto.setPushOnMention(settings.isPushOnMention());
        dto.setPushOnMessage(settings.isPushOnMessage());
        dto.setInAppOnFollow(settings.isInAppOnFollow());
        dto.setInAppOnComment(settings.isInAppOnComment());
        dto.setInAppOnLike(settings.isInAppOnLike());
        dto.setInAppOnMention(settings.isInAppOnMention());
        dto.setInAppOnMessage(settings.isInAppOnMessage());
        dto.setFcmToken(fcmToken);

        notificationService.updateNotificationSettings(userId, dto);
        return ResponseEntity.ok().build();
    }
}
