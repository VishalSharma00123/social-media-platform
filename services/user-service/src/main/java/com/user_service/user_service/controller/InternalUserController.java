package com.user_service.user_service.controller;

import com.user_service.user_service.dto.UserDTO;
import com.user_service.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    /**
     * Get user by ID
     * Called by: Message Service via Feign
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        log.info("╔════════════════════════════════════╗");
        log.info("║ INTERNAL: GET USER BY ID           ║");
        log.info("╚════════════════════════════════════╝");
        log.info("→ UserId: {}", userId);

        try {
            UserDTO user = userService.getUserById(userId);
            log.info("✓ User found: {} (ID: {})", user.getUsername(), user.getId());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("✗ Error: {}", e.getMessage(), e);
            throw new RuntimeException("User not found: " + userId);
        }
    }

    /**
     * Get multiple users by IDs
     * Called by: Message Service for batch operations
     */
    @PostMapping("/batch")
    public ResponseEntity<List<UserDTO>> getUsersByIds(@RequestBody List<String> userIds) {
        log.info("→ Getting {} users by batch", userIds.size());

        try {
            List<UserDTO> users = userService.getUsersByIds(userIds);
            log.info("✓ Found {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("✗ Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Check if user exists
     */
    @GetMapping("/{userId}/exists")
    public ResponseEntity<Boolean> userExists(@PathVariable String userId) {
        log.info("→ Checking if user exists: {}", userId);
        boolean exists = userService.userExists(userId);
        log.info("✓ User exists: {}", exists);
        return ResponseEntity.ok(exists);
    }

    /**
     * Get user's following list
     */
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<String>> getUserFollowing(@PathVariable String userId) {
        log.info("→ Getting following list for user: {}", userId);

        try {
            List<String> following = userService.getUserFollowing(userId);
            log.info("✓ User follows {} people", following.size());
            return ResponseEntity.ok(following);
        } catch (Exception e) {
            log.error("✗ Error: {}", e.getMessage(), e);
            throw e;
        }
    }
}