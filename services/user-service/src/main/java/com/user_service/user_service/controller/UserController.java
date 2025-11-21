package com.user_service.user_service.controller;

import com.user_service.user_service.dto.LoginRequest;
import com.user_service.user_service.dto.RegisterRequest;
import com.user_service.user_service.dto.UpdateProfileRequest;
import com.user_service.user_service.dto.UserDTO;
import com.user_service.user_service.service.JwtService;
import com.user_service.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request  ));
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @PostMapping("/follow/{userId}")
    public ResponseEntity<?> followUser(
            @PathVariable String userId,
            @RequestHeader("Authorization") String token) {
        String currentUserId = extractUserIdFromToken(token);
        userService.followUser(currentUserId, userId);
        return ResponseEntity.ok(Map.of("message", "Followed successfully"));
    }

    @PostMapping("/unfollow/{userId}")
    public ResponseEntity<?> unfollowUser(
            @PathVariable String userId,
            @RequestHeader("Authorization") String token) {
        String currentUserId = extractUserIdFromToken(token);
        userService.unfollowUser(currentUserId, userId);
        return ResponseEntity.ok(Map.of("message", "Unfollowed successfully"));
    }

    @PutMapping("/profile/update")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @RequestHeader("Authorization") String token) {
        String userId = extractUserIdFromToken(token);
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @PostMapping("/profile/picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        String userId = extractUserIdFromToken(token);
        String imageUrl = userService.uploadProfilePicture(userId, file);
        return ResponseEntity.ok(Map.of("profilePicture", imageUrl));
    }

    private String extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtService.extractUserId(token);
        }
        throw new RuntimeException("Invalid authorization header");
    }
}
