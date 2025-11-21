package com.message_service.message_service.repository;

import com.message_service.message_service.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {

    Page<Message> findByConversationIdOrderByCreatedAtDesc(String conversationId, Pageable pageable);

    @Query("{ $or: [ { 'senderId': ?0, 'receiverId': ?1 }, { 'senderId': ?1, 'receiverId': ?0 } ] }")
    Page<Message> findMessagesBetweenUsers(String userId1, String userId2, Pageable pageable);

    long countByReceiverIdAndIsReadFalse(String receiverId);
    Optional<Message> findByMediaUrl(String mediaUrl);

    List<Message> findByConversationIdAndReceiverIdAndIsReadFalse(String conversationId, String receiverId);
}