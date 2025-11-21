package com.admin_service.admin_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "reports")
public class Report {
    @Id
    private String id;

    private String reporterId;
    private String reportedUserId;
    private String reportedContentId;
    private ContentType contentType;

    private String reason;
    private String description;

    private ReportStatus status = ReportStatus.PENDING;
    private String reviewedBy;
    private String reviewNotes;
    private String actionTaken;

    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public enum ContentType {
        USER, POST, COMMENT, MESSAGE
    }

    public enum ReportStatus {
        PENDING, UNDER_REVIEW, RESOLVED, DISMISSED
    }
}