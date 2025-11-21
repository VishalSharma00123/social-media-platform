package com.message_service.message_service.controller;

import com.message_service.message_service.dto.MessageRequest;
import com.message_service.message_service.dto.MessageResponse;
import com.message_service.message_service.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.message_service.message_service.service.MessageService;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageRequest messageRequest,
                            SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("Method testing");
        Object userIdObj = headerAccessor.getSessionAttributes() != null ?
                headerAccessor.getSessionAttributes().get("userId") : null;
        System.out.println("DEBUG session userId: " + userIdObj);
        if (userIdObj == null) {
            // fallback (for test, set a dummy/static id)
            System.out.println("NO USER ID IN SESSION! Using test id.");
            // REMOVE this in production, only for debug sanity check
            String userId = "hardcoded-id-for-debug";
            MessageResponse response = messageService.sendMessage(userId, messageRequest);
            // ... rest of your logic still runs
            return;
        }
        String userId = userIdObj.toString();
        MessageResponse response = messageService.sendMessage(userId, messageRequest);

        WebSocketMessage wsMessage = WebSocketMessage.builder()
                .type("MESSAGE")
                .message(response)
                .build();

        messagingTemplate.convertAndSendToUser(
                messageRequest.getReceiverId(),
                "/queue/messages",
                wsMessage
        );
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload WebSocketMessage message,
                       SimpMessageHeaderAccessor headerAccessor) {
        String userId = headerAccessor.getSessionAttributes().get("userId").toString();
        message.setUserId(userId);

        messagingTemplate.convertAndSendToUser(
                message.getUserId(),
                "/queue/typing",
                message
        );
    }
}