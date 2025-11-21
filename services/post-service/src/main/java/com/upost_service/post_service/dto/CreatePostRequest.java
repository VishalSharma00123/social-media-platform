package com.upost_service.post_service.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreatePostRequest {
    @NotBlank(message = "Content is required")
    private String content;
    private String postType;
}