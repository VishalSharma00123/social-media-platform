package com.message_service.message_service.controller;

import com.message_service.message_service.client.UserServiceClient;
import com.message_service.message_service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class EurekaDebugController {

    private final DiscoveryClient discoveryClient;
    private final UserServiceClient userServiceClient;

    @GetMapping("/services")
    public Map<String, Object> getAllServices() {
        log.info("═══════════════════════════════════");
        log.info("📋 Fetching all registered services");

        List<String> services = discoveryClient.getServices();

        log.info("Found {} services: {}", services.size(), services);
        log.info("═══════════════════════════════════");

        Map<String, Object> result = new HashMap<>();
        result.put("totalServices", services.size());
        result.put("services", services);

        return result;
    }

    @GetMapping("/user-service-instances")
    public Map<String, Object> getUserServiceInstances() {
        log.info("═══════════════════════════════════");
        log.info("🔍 Checking USER-SERVICE instances...");

        List<ServiceInstance> instances = discoveryClient.getInstances("USER-SERVICE");

        log.info("Found {} USER-SERVICE instances", instances.size());

        if (instances.isEmpty()) {
            log.error("❌ NO USER-SERVICE INSTANCES FOUND!");
            log.error("This is why Feign cannot resolve the service name");
        }

        List<Map<String, Object>> instanceDetails = new ArrayList<>();

        for (ServiceInstance instance : instances) {
            log.info("Instance Details:");
            log.info("  ├─ Instance ID: {}", instance.getInstanceId());
            log.info("  ├─ Service ID: {}", instance.getServiceId());
            log.info("  ├─ Host: {}", instance.getHost());
            log.info("  ├─ Port: {}", instance.getPort());
            log.info("  ├─ URI: {}", instance.getUri());
            log.info("  ├─ Scheme: {}", instance.getScheme());
            log.info("  └─ Metadata: {}", instance.getMetadata());

            Map<String, Object> details = new HashMap<>();
            details.put("instanceId", instance.getInstanceId());
            details.put("serviceId", instance.getServiceId());
            details.put("host", instance.getHost());
            details.put("port", instance.getPort());
            details.put("uri", instance.getUri().toString());
            details.put("scheme", instance.getScheme());
            details.put("metadata", instance.getMetadata());

            instanceDetails.add(details);
        }

        log.info("═══════════════════════════════════");

        Map<String, Object> result = new HashMap<>();
        result.put("serviceName", "USER-SERVICE");
        result.put("instanceCount", instances.size());
        result.put("instances", instanceDetails);
        result.put("status", instances.isEmpty() ? "NOT_FOUND" : "FOUND");

        return result;
    }

    @GetMapping("/test-feign/{userId}")
    public Map<String, Object> testFeignCall(@PathVariable String userId) {
        log.info("═══════════════════════════════════");
        log.info("🧪 Testing Feign call for userId: {}", userId);

        Map<String, Object> result = new HashMap<>();

        try {
            // First check if service is discovered
            List<ServiceInstance> instances = discoveryClient.getInstances("USER-SERVICE");
            result.put("serviceDiscovered", !instances.isEmpty());
            result.put("instanceCount", instances.size());

            if (instances.isEmpty()) {
                log.error("❌ USER-SERVICE not found in Eureka!");
                result.put("status", "FAILED");
                result.put("error", "USER-SERVICE not registered in Eureka");
                return result;
            }

            log.info("✅ USER-SERVICE found, attempting Feign call...");

            // Try Feign call
            UserDTO user = userServiceClient.getUserById(userId);

            log.info("✅ Feign call successful!");
            log.info("  User: {} ({})", user.getUsername(), user.getId());

            result.put("status", "SUCCESS");
            result.put("user", user);

        } catch (Exception e) {
            log.error("❌ Feign call failed: {}", e.getMessage(), e);

            result.put("status", "FAILED");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());

            // Get the root cause
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            result.put("rootCause", cause.getMessage());
        }

        log.info("═══════════════════════════════════");
        return result;
    }

    @GetMapping("/health-check")
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("messageService", "UP");
        health.put("eurekaConnection", "CHECKING");

        try {
            List<String> services = discoveryClient.getServices();
            health.put("eurekaConnection", "UP");
            health.put("registeredServices", services);
            health.put("userServiceAvailable", services.contains("USER-SERVICE"));
        } catch (Exception e) {
            health.put("eurekaConnection", "DOWN");
            health.put("error", e.getMessage());
        }

        return health;
    }
}