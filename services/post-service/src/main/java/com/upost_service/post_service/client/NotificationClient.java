package com.upost_service.post_service.client;


import com.upost_service.post_service.dto.NotificationRequest;
import com.upost_service.post_service.dto.NotificationResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("NOTIFICATION-SERVICE")
public interface NotificationClient {
    @PostMapping("/internal/notifications")
    ResponseEntity<NotificationResponse>  createNotification(@Valid @RequestBody NotificationRequest request);
}
