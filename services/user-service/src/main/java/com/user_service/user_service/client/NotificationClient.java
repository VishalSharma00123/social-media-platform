package com.user_service.user_service.client;

import com.user_service.user_service.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/internal/notifications")
    void createNotification(@RequestBody NotificationRequest request);
}
