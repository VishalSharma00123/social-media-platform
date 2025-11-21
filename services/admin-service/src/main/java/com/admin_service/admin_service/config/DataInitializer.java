package com.admin_service.admin_service.config;

// DataInitializer.java
import com.admin_service.admin_service.model.Admin;
import com.admin_service.admin_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AdminService adminService;

    @Bean
    CommandLineRunner init() {
        return args -> {
            // Check if super admin exists, if not create one
            try {
                Admin superAdmin = new Admin();
                superAdmin.setUsername("superadmin");
                superAdmin.setEmail("admin@socialmedia.com");
                superAdmin.setPassword("Admin@123"); // Will be encrypted by service
                superAdmin.setFullName("Super Administrator");
                superAdmin.setRole(Admin.AdminRole.SUPER_ADMIN);

                adminService.createAdmin(superAdmin);
                System.out.println("Super admin created successfully!");
            } catch (Exception e) {
                System.out.println("Super admin already exists or error: " + e.getMessage());
            }
        };
    }
}