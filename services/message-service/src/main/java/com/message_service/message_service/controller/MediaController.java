package com.message_service.message_service.controller;

import com.message_service.message_service.model.Message;
import com.message_service.message_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Slf4j
public class MediaController {

    private final MessageRepository messageRepository;
    private static final String UPLOAD_DIR = "uploads/messages/";

    @GetMapping("/messages/{filename}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String filename,
            @AuthenticationPrincipal String userId) {

        log.info("📷 Image request: {} by user: {}", filename, userId);

        // 1. Find message with this image
        String fullUrl = "http://MESSAGE-SERVICE/api/media/messages/" + filename;
        Optional<Message> messageOpt = messageRepository.findByMediaUrl(fullUrl);

        if (messageOpt.isEmpty()) {
            log.warn("⚠️ Message not found for image: {}", filename);
            return ResponseEntity.notFound().build();
        }

        Message message = messageOpt.get();

        // 2. Check authorization (only sender or receiver can view)
        if (!message.getSenderId().equals(userId) &&
                !message.getReceiverId().equals(userId)) {
            log.warn("🚫 Unauthorized access attempt by user {} for image {}", userId, filename);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 3. Return file if authorized
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            log.error("❌ File not found on disk: {}", filePath);
            return ResponseEntity.notFound().build();
        }

        log.info("✅ Authorized access by user {} to image {}", userId, filename);

        // Determine content type based on file extension
        MediaType contentType = MediaType.IMAGE_JPEG;
        if (filename.endsWith(".png")) {
            contentType = MediaType.IMAGE_PNG;
        } else if (filename.endsWith(".gif")) {
            contentType = MediaType.IMAGE_GIF;
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .body(resource);
    }
}
