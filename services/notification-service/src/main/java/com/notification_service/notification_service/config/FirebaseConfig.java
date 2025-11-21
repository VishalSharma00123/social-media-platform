package com.notification_service.notification_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Configuration
@Slf4j  // Use proper logging instead of System.err
public class FirebaseConfig {

    @Value("${firebase.config-file:#{null}}")  // ← Returns null if not set
    private String firebaseConfigFile;

    @Value("${firebase.enabled:false}")  // ← New: enable/disable flag
    private boolean firebaseEnabled;

    @PostConstruct
    public void initialize() {
        if (!firebaseEnabled) {
            log.info("🔔 Firebase is DISABLED - Mobile push notifications will not be sent");
            log.info("   Web notifications (WebSocket) and Email notifications are still active");
            return;  // Skip initialization
        }

        if (firebaseConfigFile == null || firebaseConfigFile.isEmpty()) {
            log.warn("⚠️ Firebase config file not specified - Mobile push notifications disabled");
            return;
        }

        try {
            Resource resource = new ClassPathResource(firebaseConfigFile);

            if (!resource.exists()) {
                log.warn("⚠️ Firebase config file not found: {} - Mobile push notifications disabled",
                        firebaseConfigFile);
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase initialized successfully - Mobile push notifications enabled");
            }
        } catch (IOException e) {
            log.error("❌ Could not initialize Firebase: {}", e.getMessage());
            log.warn("⚠️ Running without Firebase - Mobile push notifications disabled");
        }
    }
}
