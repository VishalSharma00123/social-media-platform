package com.message_service.message_service.repository;

import com.message_service.message_service.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

import com.message_service.message_service.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends MongoRepository<Conversation, String> {

    /**
     * ✅ RECOMMENDED: Find conversation by exact participants match
     * Use this when participants are ALWAYS sorted before querying
     * This is the FASTEST method since it uses exact array matching
     */
    Optional<Conversation> findByParticipants(List<String> participants);

    /**
     * ✅ ALTERNATIVE: Find conversation regardless of participant order
     * Use this if you don't want to sort participants first
     * Slightly slower than findByParticipants() due to $all operator
     */
    @Query("{ 'participants': { $all: ?0, $size: ?1 } }")
    Optional<Conversation> findByParticipantsIgnoreOrder(List<String> participants, int size);

    /**
     * ✅ FIXED: Find all conversations where user is a participant
     * Correctly uses MongoDB array query operator
     */
    @Query("{ 'participants': { $in: [?0] } }")
    List<Conversation> findByParticipantsContaining(String userId);

    /**
     * ✅ ALTERNATIVE (Spring Data JPA naming convention)
     * This works without @Query annotation due to Spring Data magic
     * Use either this OR the @Query version above, not both
     */
    // List<Conversation> findByParticipantsContaining(String userId);

    /**
     * ✅ Find conversations updated after a certain time (useful for sync)
     */
    @Query("{ 'participants': { $in: [?0] }, 'updatedAt': { $gte: ?1 } }")
    List<Conversation> findRecentConversations(String userId, java.time.LocalDateTime after);

}
