package com.message_service.message_service.config;

import com.message_service.message_service.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("🔌 WebSocket CONNECT request received");

            String authToken = accessor.getFirstNativeHeader("Authorization");

            if (authToken != null && authToken.startsWith("Bearer ")) {
                String token = authToken.substring(7);

                try {
                    String userId = jwtAuthenticationFilter.extractUserId(token);

                    if (userId != null && jwtAuthenticationFilter.isTokenValid(token)) {
                        accessor.getSessionAttributes().put("userId", userId);
                        log.info("✅ WebSocket authenticated for user: {}", userId);
                    } else {
                        log.warn("❌ Invalid WebSocket token");
                        throw new IllegalArgumentException("Invalid token");
                    }
                } catch (Exception e) {
                    log.error("❌ WebSocket authentication failed: {}", e.getMessage());
                    throw new IllegalArgumentException("Authentication failed");
                }
            } else {
                log.warn("❌ No Authorization header in WebSocket connection");
                throw new IllegalArgumentException("Missing authorization");
            }
        }

        return message;
    }
}
