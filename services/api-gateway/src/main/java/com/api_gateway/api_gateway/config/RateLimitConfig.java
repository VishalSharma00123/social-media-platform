package com.api_gateway.api_gateway.config;

import com.api_gateway.api_gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Autowired
    private JwtUtil jwtUtil;  // Proper dependency injection

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(
                10,  // replenishRate: tokens added per second
                20   // burstCapacity: max tokens in bucket
        );
    }

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Extract user ID from JWT token
            String token = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                try {
                    // Remove "Bearer " prefix
                    String jwt = token.substring(7);

                    // Validate token first
                    if (jwtUtil.validateToken(jwt)) {
                        String userId = jwtUtil.getUserId(jwt);  // Changed from extractUserId
                        return Mono.just(userId);
                    }
                } catch (Exception e) {
                    System.err.println("Error extracting user ID from token: " + e.getMessage());
                }
            }

            // Fallback to IP address for non-authenticated users
            return Mono.just(
                    exchange.getRequest()
                            .getRemoteAddress()
                            .getAddress()
                            .getHostAddress()
            );
        };
    }
}