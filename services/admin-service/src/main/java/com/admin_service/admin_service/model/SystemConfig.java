package com.admin_service.admin_service.model;

// SystemConfig.java

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "system_configs")
public class SystemConfig {
    @Id
    private String id;

    @Indexed(unique = true)
    private String key;

    private String value;
    private String description;
    private ConfigType type;

    private LocalDateTime updatedAt;

    private String updatedBy;

    public enum ConfigType {
        STRING, NUMBER, BOOLEAN, JSON
    }
}
