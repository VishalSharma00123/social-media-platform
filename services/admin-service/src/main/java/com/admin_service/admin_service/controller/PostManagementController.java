package com.admin_service.admin_service.controller;

import com.admin_service.admin_service.client.PostServiceClient;
import com.admin_service.admin_service.dto.PostManagementDTO;
import com.admin_service.admin_service.dto.ReportedPostDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
public class PostManagementController {

    private final PostServiceClient postServiceClient;

    @GetMapping
    public ResponseEntity<Page<PostManagementDTO>> getPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postServiceClient.searchPosts(search, userId, pageable));
    }

    @GetMapping("/reported")
    public ResponseEntity<Page<ReportedPostDTO>> getReportedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postServiceClient.getReportedPosts(pageable));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable String postId) {
        postServiceClient.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/hide")
    public ResponseEntity<Void> hidePost(@PathVariable String postId) {
        postServiceClient.hidePost(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/unhide")
    public ResponseEntity<Void> unhidePost(@PathVariable String postId) {
        postServiceClient.unhidePost(postId);
        return ResponseEntity.ok().build();
    }
}