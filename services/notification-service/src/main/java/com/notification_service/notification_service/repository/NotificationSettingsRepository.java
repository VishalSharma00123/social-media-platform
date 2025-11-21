package com.notification_service.notification_service.repository;


// NotificationSettingsRepository.java

import com.notification_service.notification_service.model.NotificationSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface NotificationSettingsRepository extends MongoRepository<NotificationSettings, String> {
    Optional<NotificationSettings> findByUserId(String userId);
}

