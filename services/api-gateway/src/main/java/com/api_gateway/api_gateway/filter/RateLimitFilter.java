package com.api_gateway.api_gateway.filter;

import com.api_gateway.api_gateway.config.GatewayConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@Slf4j
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> implements GatewayFilter {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    // ✅ FIX: Proper constructor with super() call
    public RateLimitFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Extract client IP (consider proxy headers)
            String clientIp = getClientIp(exchange);
            String key = "rate_limit:" + clientIp;

            log.debug("🚦 Rate limit check for IP: {} (limit: {} req/min)",
                    clientIp, config.getRequestsPerMinute());

            return redisTemplate.opsForValue()
                    .increment(key)
                    .flatMap(count -> {
                        // Set expiration on first request
                        if (count == 1) {
                            return redisTemplate.expire(key, Duration.ofMinutes(1))
                                    .then(Mono.just(count));
                        }
                        return Mono.just(count);
                    })
                    .flatMap(count -> {
                        // Check if limit exceeded
                        if (count > config.getRequestsPerMinute()) {
                            log.warn("🚫 Rate limit exceeded for IP: {} (requests: {}/{})",
                                    clientIp, count, config.getRequestsPerMinute());

                            // Set response status and headers
                            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

                            // Add rate limit headers
                            exchange.getResponse().getHeaders().add("X-RateLimit-Limit",
                                    String.valueOf(config.getRequestsPerMinute()));
                            exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", "0");
                            exchange.getResponse().getHeaders().add("X-RateLimit-Reset", "60");
                            exchange.getResponse().getHeaders().add("Retry-After", "60");

                            // Return error response body
                            String errorMessage = String.format(
                                    "{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded. Maximum %d requests per minute allowed.\",\"retryAfter\":60}",
                                    config.getRequestsPerMinute()
                            );

                            return exchange.getResponse()
                                    .writeWith(Mono.just(exchange.getResponse()
                                            .bufferFactory()
                                            .wrap(errorMessage.getBytes())));
                        }

                        // Add rate limit info headers to successful response
                        long remaining = config.getRequestsPerMinute() - count;
                        exchange.getResponse().getHeaders().add("X-RateLimit-Limit",
                                String.valueOf(config.getRequestsPerMinute()));
                        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining",
                                String.valueOf(Math.max(0, remaining)));

                        log.debug("✅ Request allowed for IP: {} ({}/{})",
                                clientIp, count, config.getRequestsPerMinute());

                        return chain.filter(exchange);
                    })
                    .onErrorResume(e -> {
                        log.error("❌ Error in rate limiting for IP: {}", clientIp, e);
                        // Fail open: allow request if Redis fails
                        return chain.filter(exchange);
                    });
        };
    }

    /**
     * Extract client IP from request, considering proxy headers
     */
    private String getClientIp(org.springframework.web.server.ServerWebExchange exchange) {
        // Check X-Forwarded-For header (from load balancer/proxy)
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        // Check X-Real-IP header (from nginx)
        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // Fallback to remote address
        return exchange.getRequest().getRemoteAddress() != null
                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // your rate limiting logic here
        return chain.filter(exchange);
    }

    @Data
    public static class Config {
        private int requestsPerMinute = 60; // Default: 60 requests per minute
    }
}