package com.message_service.message_service.service;

import com.message_service.message_service.client.UserServiceClient;
import com.message_service.message_service.dto.ConversationResponse;
import com.message_service.message_service.dto.UserDTO;
import com.message_service.message_service.dto.WebSocketMessage;
import com.message_service.message_service.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.message_service.message_service.model.Conversation;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.message_service.message_service.repository.ConversationRepository;
import com.message_service.message_service.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserServiceClient userServiceClient;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * ✅ SINGLE SOURCE OF TRUTH for finding/creating conversations
     */
    public Conversation findOrCreateConversation(String userId1, String userId2) {
        // Sort participants for consistency
        List<String> participants = Arrays.asList(userId1, userId2);
        Collections.sort(participants);

        // Try to find existing conversation
        Optional<Conversation> existingConversation = conversationRepository
                .findByParticipants(participants);

        if (existingConversation.isPresent()) {
            log.info("Found existing conversation: {}", existingConversation.get().getId());
            return existingConversation.get();
        }

        // Create new conversation
        log.info("Creating new conversation between: {}", participants);
        Conversation conversation = new Conversation();
        conversation.setParticipants(participants);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setUnreadCount(new HashMap<>());

        Conversation saved = conversationRepository.save(conversation);
        log.info("Created conversation with ID: {}", saved.getId());
        return saved;
    }

    /**
     * ✅ Get conversation details for display
     */
    public ConversationResponse getOrCreateConversation(String userId1, String userId2) {
        Conversation conversation = findOrCreateConversation(userId1, userId2);
        return mapToConversationResponse(conversation, userId1);
    }

    /**
     * ✅ SINGLE METHOD to mark conversation as read
     */
    public void markAsRead(String conversationId, String userId) {
        log.info("Marking conversation {} as read for user {}", conversationId, userId);

        // Verify conversation and user participation
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        if (!conversation.getParticipants().contains(userId)) {
            throw new RuntimeException("User not part of this conversation");
        }

        // Mark messages as read
        List<Message> unreadMessages = messageRepository
                .findByConversationIdAndReceiverIdAndIsReadFalse(conversationId, userId);

        if (!unreadMessages.isEmpty()) {
            LocalDateTime readTime = LocalDateTime.now();
            unreadMessages.forEach(message -> {
                message.setRead(true);
                message.setReadAt(readTime);
            });
            messageRepository.saveAll(unreadMessages);
            log.info("Marked {} messages as read", unreadMessages.size());

            // Send WebSocket read receipts
            sendReadReceiptNotifications(unreadMessages, userId, conversationId);
        }

        // Reset conversation unread count
        updateConversationUnreadCount(conversation, userId, 0);
    }

    /**
     * ✅ Update conversation after sending message
     */
    public void updateAfterMessage(Conversation conversation, Message message, String receiverId) {
        conversation.setLastMessage(message.getContent());
        conversation.setLastMessageSenderId(message.getSenderId());
        conversation.setLastMessageTime(message.getCreatedAt());
        conversation.setUpdatedAt(LocalDateTime.now());

        // Increment unread count
        Map<String, Integer> unreadCount = conversation.getUnreadCount();
        if (unreadCount == null) {
            unreadCount = new HashMap<>();
        }
        unreadCount.put(receiverId, unreadCount.getOrDefault(receiverId, 0) + 1);
        conversation.setUnreadCount(unreadCount);

        conversationRepository.save(conversation);
        log.info("Conversation updated: {}", conversation.getId());
    }

    /**
     * ✅ Helper to update unread count
     */
    private void updateConversationUnreadCount(Conversation conversation, String userId, int count) {
        Map<String, Integer> unreadCount = conversation.getUnreadCount();
        if (unreadCount == null) {
            unreadCount = new HashMap<>();
        }
        unreadCount.put(userId, count);
        conversation.setUnreadCount(unreadCount);
        conversationRepository.save(conversation);
    }

    /**
     * ✅ Send read receipts via WebSocket
     */
    private void sendReadReceiptNotifications(List<Message> messages, String readerId, String conversationId) {
        messages.forEach(message -> {
            try {
                WebSocketMessage wsMessage = WebSocketMessage.builder()
                        .type("READ")
                        .conversationId(conversationId)
                        .messageId(message.getId())
                        .userId(readerId)
                        .timestamp(System.currentTimeMillis())
                        .build();

                messagingTemplate.convertAndSendToUser(
                        message.getSenderId(),
                        "/queue/messages",
                        wsMessage
                );
            } catch (Exception e) {
                log.error("Failed to send read receipt: {}", e.getMessage());
            }
        });
    }

    public List<ConversationResponse> getUserConversations(String userId) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContaining(userId);
        return conversations.stream()
                .map(conversation -> mapToConversationResponse(conversation, userId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public int getTotalUnreadCount(String userId) {
        List<Conversation> conversations = conversationRepository.findByParticipantsContaining(userId);
        return conversations.stream()
                .mapToInt(conversation -> {
                    Map<String, Integer> unreadCount = conversation.getUnreadCount();
                    return unreadCount != null ? unreadCount.getOrDefault(userId, 0) : 0;
                })
                .sum();
    }

    private ConversationResponse mapToConversationResponse(Conversation conversation, String currentUserId) {
        try {
            String otherUserId = conversation.getParticipants().stream()
                    .filter(id -> !id.equals(currentUserId))
                    .findFirst()
                    .orElse(null);

            if (otherUserId == null) {
                log.warn("Could not find other user in conversation: {}", conversation.getId());
                return null;
            }

            UserDTO otherUser = userServiceClient.getUserById(otherUserId);
            if (otherUser == null) {
                log.warn("User not found: {}", otherUserId);
                return null;
            }

            int unreadCount = conversation.getUnreadCount() != null
                    ? conversation.getUnreadCount().getOrDefault(currentUserId, 0)
                    : 0;

            return ConversationResponse.builder()
                    .id(conversation.getId())
                    .otherUserId(otherUserId)
                    .otherUserName(otherUser.getUsername())
                    .otherUserProfilePicture(otherUser.getProfilePicture())
                    .lastMessage(conversation.getLastMessage())
                    .lastMessageTime(conversation.getLastMessageTime())
                    .unreadCount(unreadCount)
                    .isOnline(false)
                    .build();
        } catch (Exception e) {
            log.error("Error mapping conversation: {}", e.getMessage());
            return null;
        }
    }
}
