package com.message_service.message_service.controller;

import com.message_service.message_service.dto.ConversationResponse;
import com.message_service.message_service.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getUserConversations(
            @AuthenticationPrincipal String userId) {
        List<ConversationResponse> conversations =
                conversationService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{otherUserId}")
    public ResponseEntity<ConversationResponse> getOrCreateConversation(
            @AuthenticationPrincipal String userId,
            @PathVariable String otherUserId) {
        ConversationResponse conversation =
                conversationService.getOrCreateConversation(userId, otherUserId);
        return ResponseEntity.ok(conversation);
    }

    @PutMapping("/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String conversationId,
            @AuthenticationPrincipal String userId) {
        conversationService.markAsRead(conversationId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getTotalUnreadCount(
            @AuthenticationPrincipal String userId) {
        int count = conversationService.getTotalUnreadCount(userId);
        return ResponseEntity.ok(count);
    }
}