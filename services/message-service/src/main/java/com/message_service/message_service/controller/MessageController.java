package com.message_service.message_service.controller;

// MessageController.java
import com.message_service.message_service.model.Message;
import com.message_service.message_service.repository.MessageRepository;
import com.message_service.message_service.service.ConversationService;
import com.message_service.message_service.dto.MessageRequest;
import com.message_service.message_service.dto.MessageResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.message_service.message_service.service.MessageService;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;


    private final MessageRepository messageRepository;

    private static final String UPLOAD_DIR = "uploads/messages/";


    // ✅ Add constructor to create directory
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            log.info("✅ Upload directory created: {}", UPLOAD_DIR);
        } catch (IOException e) {
            log.error("❌ Failed to create upload directory: {}", e.getMessage());
        }
    }

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal String senderId,
            @RequestBody MessageRequest request) {
        MessageResponse response = messageService.sendMessage(senderId, request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/messages/{filename}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String filename,
            @AuthenticationPrincipal String userId) {  // ✅ Authentication required!

        log.info("📷 Image request: {} by user: {}", filename, userId);

        // 1. Find message with this image
        String fullUrl = "http://MESSAGE-SERVICE/uploads/messages/" + filename;
        Message message = messageRepository.findByMediaUrl(fullUrl)
                .orElse(null);

        if (message == null) {
            log.warn("⚠️ Image not found: {}", filename);
            return ResponseEntity.notFound().build();
        }

        // 2. Check authorization (only sender or receiver can view)
        if (!message.getSenderId().equals(userId) &&
                !message.getReceiverId().equals(userId)) {
            log.warn("🚫 Unauthorized access attempt by user {} for image {}", userId, filename);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 3. Return file if authorized
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        org.springframework.core.io.Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            log.error("❌ File not found on disk: {}", filePath);
            return ResponseEntity.notFound().build();
        }

        log.info("✅ Authorized access by user {} to image {}", userId, filename);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)  // Adjust based on file type
                .body(resource);
    }

    @PostMapping("/send-with-media")
    public ResponseEntity<MessageResponse> sendMessageWithMedia(
            @AuthenticationPrincipal String senderId,
            @RequestParam("receiverId") String receiverId,
            @RequestParam("content") String content,
            @RequestParam("type") String type,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        log.info("📤 Received media upload request from user: {}", senderId);
        log.info("   Receiver: {}", receiverId);
        log.info("   Type: {}", type);
        log.info("   File: {}", file != null ? file.getOriginalFilename() : "none");

        String mediaUrl = null;

        if (file != null && !file.isEmpty()) {
            try {
                // Create directory if doesn't exist
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                    log.info("📁 Created upload directory: {}", UPLOAD_DIR);
                }

                // Validate file
                if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                    log.error("❌ File too large: {} bytes", file.getSize());
                    return ResponseEntity.badRequest().build();
                }

                // Generate filename
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String filename = UUID.randomUUID().toString() + extension;

                // Save file
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                mediaUrl = "http://MESSAGE-SERVICE/api/media/messages/" + filename;

                log.info("✅ File uploaded successfully: {}", filename);
                log.info("   Size: {} bytes", file.getSize());
                log.info("   URL: {}", mediaUrl);

            } catch (IOException e) {
                log.error("❌ File upload failed: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError().build();
            }
        }

        // Create message request
        MessageRequest request = new MessageRequest();
        request.setReceiverId(receiverId);
        request.setContent(content);
        request.setType(type);
        request.setMediaUrl(mediaUrl);

        MessageResponse response = messageService.sendMessage(senderId, request);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Page<MessageResponse>> getConversationMessages(
            @PathVariable String conversationId,
            @AuthenticationPrincipal String userId,
            Pageable pageable) {
        Page<MessageResponse> messages = messageService.getConversationMessages(conversationId, userId, pageable);
        return ResponseEntity.ok(messages);
    }
}
