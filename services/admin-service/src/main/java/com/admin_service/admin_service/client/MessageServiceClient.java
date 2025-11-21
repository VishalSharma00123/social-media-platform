package com.admin_service.admin_service.client;

import com.admin_service.admin_service.dto.ConversationDetailsDTO;
import com.admin_service.admin_service.dto.MessageStatsResponse;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "message-service")
public interface MessageServiceClient {

    @GetMapping("/api/messages/admin/statistics")
    MessageStatsResponse getMessageStatistics();

    @GetMapping("/api/messages/admin/conversations/{conversationId}")
    ConversationDetailsDTO getConversationDetails(@PathVariable String conversationId);

    @DeleteMapping("/api/messages/admin/{messageId}")
    void deleteMessage(@PathVariable String messageId);
}
