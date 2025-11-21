package com.admin_service.admin_service.service;


import com.admin_service.admin_service.model.Admin;
import com.admin_service.admin_service.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public Admin createAdmin(Admin admin) {
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        // Set default permissions based on role
        admin.setPermissions(getDefaultPermissions(admin.getRole()));

        return adminRepository.save(admin);
    }

    public Admin updateAdminRole(String adminId, Admin.AdminRole newRole) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setRole(newRole);
        admin.setPermissions(getDefaultPermissions(newRole));
        admin.setUpdatedAt(LocalDateTime.now());

        return adminRepository.save(admin);
    }

    private Set<String> getDefaultPermissions(Admin.AdminRole role) {
        switch (role) {
            case SUPER_ADMIN:
                return Set.of("ALL");
            case ADMIN:
                return Set.of("USER_MANAGE", "POST_MANAGE", "REPORT_MANAGE", "VIEW_STATS");
            case MODERATOR:
                return Set.of("POST_MANAGE", "REPORT_MANAGE", "VIEW_STATS");
            case SUPPORT:
                return Set.of("REPORT_MANAGE", "VIEW_STATS");
            default:
                return Set.of();
        }
    }
}
