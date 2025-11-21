package com.user_service.user_service.service;

import io.jsonwebtoken.Claims;
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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
        String method = request.getMethod();

        boolean shouldSkip = path.startsWith("/api/users/internal") ||  // Removed trailing slash
                path.startsWith("/api/users/register") ||
                path.startsWith("/api/users/login") ||
                path.startsWith("/api/users/test") ||
                path.startsWith("/actuator");

        if (shouldSkip) {
            log.info("⏭️  SKIPPING JWT authentication for: {} {}", method, path);
        } else {
            log.debug("🔐 JWT authentication required for: {} {}", method, path);
        }

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("🔍 JWT Filter: Processing {}", path);

        try {
            String jwt = extractJwtFromRequest(request);

            if (jwt != null) {
                log.debug("🎫 JWT token found, validating...");
                String userId = validateTokenAndGetUserId(jwt);

                if (userId != null) {
                    log.debug("✅ JWT valid for userId: {}", userId);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    new ArrayList<>()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("✅ User {} authenticated via JWT", userId);
                } else {
                    log.warn("❌ JWT validation failed - invalid token");
                }
            } else {
                log.debug("ℹ️  No JWT token found in Authorization header");
            }
        } catch (Exception e) {
            log.error("❌ JWT authentication failed: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.debug("📝 Extracted token: {}...", token.substring(0, Math.min(20, token.length())));
            return token;
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

            String userId = claims.getSubject();
            log.debug("📋 Token claims - Subject: {}, Username: {}",
                    userId, claims.get("username"));

            return userId;
        } catch (Exception e) {
            log.error("❌ Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }
}