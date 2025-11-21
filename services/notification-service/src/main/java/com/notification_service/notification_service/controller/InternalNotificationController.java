    package com.notification_service.notification_service.controller;

    import com.notification_service.notification_service.dto.NotificationRequest;
    import com.notification_service.notification_service.dto.NotificationResponse;
    import com.notification_service.notification_service.service.NotificationService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import jakarta.validation.Valid;

    /**
     * INTERNAL API - Only accessible by other microservices
     * Should NOT be exposed through API Gateway
     */
    @RestController
    @RequestMapping("/internal/notifications")  // ← Internal prefix
    @RequiredArgsConstructor
    public class InternalNotificationController {

        private final NotificationService notificationService;

        /**
         * Called by other services (User Service, Post Service, etc.)
         * to create notifications
         */
        @PostMapping
        public ResponseEntity<NotificationResponse> createNotification(
                @Valid @RequestBody NotificationRequest request) {
            return ResponseEntity.ok(notificationService.createNotification(request));
        }
    }
