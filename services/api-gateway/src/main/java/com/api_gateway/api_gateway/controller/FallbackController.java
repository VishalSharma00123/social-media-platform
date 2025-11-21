package com.api_gateway.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user")
    public Mono<Map<String, String>> userServiceFallback() {
        return Mono.just(Map.of(
                "message", "User service is temporarily unavailable",
                "status", "503"
        ));
    }

    @GetMapping("/post")
    public Mono<Map<String, String>> postServiceFallback() {
        return Mono.just(Map.of(
                "message", "Post service is temporarily unavailable",
                "status", "503"
        ));
    }

    @GetMapping("/message")
    public Mono<Map<String, String>> messageServiceFallback() {
        return Mono.just(Map.of(
                "message", "Message service is temporarily unavailable",
                "status", "503"
        ));
    }

    @GetMapping("/notification")
    public Mono<Map<String, String>> notificationServiceFallback() {
        return Mono.just(Map.of(
                "message", "Notification service is temporarily unavailable",
                "status", "503"
        ));
    }

    @GetMapping("/admin")
    public Mono<Map<String, String>> adminServiceFallback() {
        return Mono.just(Map.of(
                "message", "Admin service is temporarily unavailable",
                "status", "503"
        ));
    }
}