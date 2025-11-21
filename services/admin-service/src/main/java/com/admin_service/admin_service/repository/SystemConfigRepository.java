package com.admin_service.admin_service.repository;

import com.admin_service.admin_service.model.SystemConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface SystemConfigRepository extends MongoRepository<SystemConfig, String> {
    Optional<SystemConfig> findByKey(String key);
}