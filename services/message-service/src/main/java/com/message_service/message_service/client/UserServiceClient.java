package com.message_service.message_service.client;


import com.message_service.message_service.config.FeignConfig;
import com.message_service.message_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "USER-SERVICE",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/users/internal/{userId}")
    UserDTO getUserById(@PathVariable("userId") String userId);

    @PostMapping("/api/users/internal/batch")
    List<UserDTO> getUsersByIds(@RequestBody List<String> userIds);
}