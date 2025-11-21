package com.upost_service.post_service.client;

import com.upost_service.post_service.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/internal/{userId}")
    UserDTO getUserById(@PathVariable String userId);

    @GetMapping("/api/users/internal/{userId}/following")
    List<String> getUserFollowing(@PathVariable String userId);
}
