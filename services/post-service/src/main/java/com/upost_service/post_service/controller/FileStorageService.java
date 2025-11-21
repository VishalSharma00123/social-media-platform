package com.upost_service.post_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    // Base directory to store files, configure via application.properties
    @Value("${upload.path:uploads}")
    private String uploadDir;

    // Save image file and return relative URL/path
    public String saveImage(MultipartFile file) {
        return saveFile(file, "images");
    }

    // Save video file and return relative URL/path
    public String saveVideo(MultipartFile file) {
        return saveFile(file, "videos");
    }

    // General method to save file in subfolder and return URL
    private String saveFile(MultipartFile file, String subfolder) {
        try {
            String ext = getFileExtension(file.getOriginalFilename());
            String uniqueName = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);
            Path dir = Path.of(uploadDir, subfolder);
            Files.createDirectories(dir);
            Path filePath = dir.resolve(uniqueName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            // Return relative path for serving, e.g. "/uploads/images/abc123.jpg"
            return "/uploads/" + subfolder + "/" + uniqueName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
    }

    private String getFileExtension(String filename) {
        int idx = filename.lastIndexOf(".");
        return (idx > 0) ? filename.substring(idx + 1) : "";
    }
}

