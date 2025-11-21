package com.message_service.message_service.dto;

    import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageStatusUpdate {
    private String messageId;
    private String status; // SENT, DELIVERED, READ
    private String userId;
    private LocalDateTime timestamp;
}
