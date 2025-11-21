package com.admin_service.admin_service.controller;

// UserManagementController.java

import com.admin_service.admin_service.dto.AdminActionRequest;
import com.admin_service.admin_service.dto.UserManagementDto;
import com.admin_service.admin_service.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<Page<UserManagementDto>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userManagementService.getAllUsers(search, pageable));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserManagementDto> getUserDetails(@PathVariable String userId) {
        return ResponseEntity.ok(userManagementService.getUserDetails(userId));
    }

    @PostMapping("/action")
    @PreAuthorize("hasPermission(#request.targetId, 'USER', 'MANAGE')")
    public ResponseEntity<Void> performAction(
            @Valid @RequestBody AdminActionRequest request,
            @AuthenticationPrincipal String adminId) {
        userManagementService.performUserAction(request, adminId);
        return ResponseEntity.ok().build();
    }
}

