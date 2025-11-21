package com.api_gateway.api_gateway.config;

import com.api_gateway.api_gateway.filter.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class    GatewayConfig {

    private final RateLimitFilter rateLimitFilter;
    private final RedisRateLimiter redisRateLimiter;
    private final KeyResolver userKeyResolver;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(userKeyResolver)))
                        .uri("lb://USER-SERVICE"))

                // Post Service
                .route("post-service", r -> r
                        .path("/api/posts/**")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(userKeyResolver)))
                        .uri("lb://POST-SERVICE"))

                .route("post-service-files", r -> r
                        .path("/uploads/**")
                        .uri("lb://POST-SERVICE"))

                // Message Service
                .route("message-service-messages", r -> r
                        .path("/api/messages/**")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(userKeyResolver)))
                        .uri("lb://MESSAGE-SERVICE"))

                // /api/conversations
                .route("message-service-conversations", r-> r
                        .path("/api/conversations/**")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(userKeyResolver)))
                        .uri("lb://MESSAGE-SERVICE"))
                // Notification Service
//                Notification Service - User endpoints (require JWT)
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .filters(f -> f
                                .preserveHostHeader()
                                .requestRateLimiter(c -> c
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(userKeyResolver)))
                        .uri("lb://NOTIFICATION-SERVICE"))

                .route("notification-service", r -> r
                        .path("/internal/notifications/**")
                        .filters(f -> f
                                .preserveHostHeader()
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(userKeyResolver)))
                        .uri("lb://NOTIFICATION-SERVICE"))

                .route("message-service-websocket", r -> r
                        .path("/ws/**")
                        .uri("lb:ws://MESSAGE-SERVICE"))  // ← Note: lb:ws://


                // Admin Service
                .route("admin-service", r -> r
                        .path("/api/admin/**")
                        .filters(f -> f.requestRateLimiter(c -> c
                                .setRateLimiter(redisRateLimiter)
                                .setKeyResolver(userKeyResolver)))
                        .uri("lb://admin-service"))

                .build();
    }
}