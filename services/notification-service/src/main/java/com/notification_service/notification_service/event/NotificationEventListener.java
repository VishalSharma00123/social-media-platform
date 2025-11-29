package com.notification_service.notification_service.event;

import com.notification_service.notification_service.dto.LoginNotificationRequest;
import com.notification_service.notification_service.dto.NotificationRequest;
import com.notification_service.notification_service.dto.RegistrationNotificationRequest;
import com.notification_service.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "user-events", groupId = "notification-service-v3")
    public void handleUserEvent(UserEvent event) {
        log.info("Received user event: {}", event);

        switch (event.getType()) {
            case "FOLLOW":
                createFollowNotification(event);
                break;
            case "LOGIN":
                createLoginNotification(event);
                break;
            case "REGISTRATION":
                createRegistrationNotification(event);
        }
    }

    @KafkaListener(topics = "post-events", groupId = "notification-service-v3")
    public void handlePostEvent(PostEvent event) {
        log.info("Received post event: {}", event);

        switch (event.getType()) {
            case "POST_LIKED":
                createLikeNotification(event);
                break;
            case "COMMENT":
                createCommentNotification(event);
                break;
        }
    }

    @KafkaListener(topics = "message-events", groupId = "notification-service-v3")
    public void handleMessageEvent(MessageEvent event) {
        log.info("Received message event: {}", event);

        if ("NEW_MESSAGE".equals(event.getType())) {
            createMessageNotification(event);
        }
    }

    private void createFollowNotification(UserEvent event) {
        NotificationRequest request = NotificationRequest.builder()
                .userId(event.getTargetUserId())
                .senderId(event.getUserId())
                .type("FOLLOW")
                .title("New Follower")
                .message(event.getUsername() + " started following you")
                .targetId(event.getUserId())
                .build();

        notificationService.createNotification(request);
    }

    private void createRegistrationNotification(UserEvent event){
        RegistrationNotificationRequest request = RegistrationNotificationRequest.builder()
                .userId(event.getUserId())
                .type("REGISTRATION")
                .title("New Registration")
                .message("A new user has registered")
                .build();

        notificationService.createRegistrationNotification(request);
    }

    private void createLoginNotification(UserEvent user){
        LoginNotificationRequest request = LoginNotificationRequest.builder()
                .userId(user.getUserId())
                .type("LOGIN")
                .title("New Login")
                .message(user.getUsername()+" has logined ")
                .build();

        notificationService.createLoginNotification(request);

    }
    private void createLikeNotification(PostEvent event) {
        NotificationRequest request = NotificationRequest.builder()
                .userId(event.getPostOwnerId())
                .senderId(event.getUserId())
                .type("LIKE_POST")
                .title("New Like")
                .message(event.getUsername() + " liked your post")
                .targetId(event.getPostId())
                .build();

        notificationService.createNotification(request);
    }

    private void createCommentNotification(PostEvent event) {
        NotificationRequest request = NotificationRequest.builder()
                .userId(event.getPostOwnerId())
                .senderId(event.getUserId())
                .type("COMMENT")
                .title("New Comment")
                .message(event.getUsername() + " commented on your post")
                .targetId(event.getPostId())
                .metadata(Map.of("comment", event.getComment()))
                .build();

        notificationService.createNotification(request);
    }

    private void createMessageNotification(MessageEvent event) {
        NotificationRequest request = NotificationRequest.builder()
                .userId(event.getReceiverId())
                .senderId(event.getSenderId())
                .type("MESSAGE")
                .title("New Message")
                .message(event.getSenderName() + " sent you a message")
                .targetId(event.getConversationId())
                .build();

        notificationService.createNotification(request);
    }
}



