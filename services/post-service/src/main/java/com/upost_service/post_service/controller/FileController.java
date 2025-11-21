package com.upost_service.post_service.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class FileController {

    @GetMapping("/uploads/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String type, @PathVariable String filename) {
        Path file = Path.of("uploads", type, filename);
        Resource resource = new org.springframework.core.io.FileSystemResource(file.toFile());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        try {
            return ResponseEntity.ok()
                    .header("Content-Type", Files.probeContentType(file))
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
