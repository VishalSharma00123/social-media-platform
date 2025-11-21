package com.message_service.message_service.model;

// Message.java
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
@CompoundIndexes({
        @CompoundIndex(name = "conversation_index", def = "{'conversationId': 1, 'createdAt': -1}")
})
public class Message {
    @Id
    private String id;

    private String conversationId;
    private String senderId;
    private String senderName;
    private String senderProfilePicture;
    private String receiverId;

    private String content;
    private MessageType type = MessageType.TEXT;
    private String mediaUrl;

    private boolean isRead = false;
    private boolean isDeleted = false;

    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public enum MessageType {
        TEXT, IMAGE, VIDEO, FILE
    }
}
