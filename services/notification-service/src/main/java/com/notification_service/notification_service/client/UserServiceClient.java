package com.notification_service.notification_service.client;

import com.notification_service.notification_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @GetMapping("/api/users/internal/{userId}")
    UserDTO getUserById(@PathVariable String userId);
}
