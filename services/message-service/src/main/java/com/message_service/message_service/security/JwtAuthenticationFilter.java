package com.message_service.message_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // ✅ Fixed: removed trailing slash
        return path.startsWith("/ws") ||
                path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        log.debug("════════════════════════════════════");
        log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            String jwt = extractJwtFromRequest(request);

            if (jwt != null) {
                log.debug("JWT Token found");

                String userId = validateTokenAndGetUserId(jwt);

                if (userId != null) {
                    log.debug("✓ Token validated, UserId: {}", userId);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    new ArrayList<>()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("✓ Authentication set in SecurityContext");
                } else {
                    log.warn("✗ UserId is NULL after token validation");
                }
            } else {
                log.warn("⚠ No JWT token found in request");
            }
        } catch (Exception e) {
            log.error("✗ Authentication failed: {}", e.getMessage());
        }

        log.debug("════════════════════════════════════");
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private String validateTokenAndGetUserId(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.debug("→ Claims extracted successfully");

            String userId = claims.getSubject();
            if (userId == null) {
                userId = claims.get("userId", String.class);
            }

            return userId;

        } catch (ExpiredJwtException e) {
            log.error("✗ JWT token is expired: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.error("✗ Invalid JWT token: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("✗ Unexpected error validating token: {}", e.getMessage());
            return null;
        }
    }

    // ✅ Add this public method for WebSocketAuthInterceptor to use
    public String extractUserId(String token) {
        return validateTokenAndGetUserId(token);
    }

    // ✅ Add token validation method
    public boolean isTokenValid(String token) {
        return validateTokenAndGetUserId(token) != null;
    }
}
