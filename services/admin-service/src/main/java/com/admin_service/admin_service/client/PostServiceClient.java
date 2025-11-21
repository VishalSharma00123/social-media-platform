package com.admin_service.admin_service.client;

import com.admin_service.admin_service.dto.PostManagementDTO;
import com.admin_service.admin_service.dto.PostStatsResponse;
import com.admin_service.admin_service.dto.ReportedPostDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "post-service")
public interface PostServiceClient {

    @GetMapping("/api/posts/admin/statistics")
    PostStatsResponse getPostStatistics();

    @GetMapping("/api/posts/admin/search")
    Page<PostManagementDTO> searchPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String userId,
            @RequestParam Pageable pageable
    );

    @DeleteMapping("/api/posts/admin/{postId}")
    void deletePost(@PathVariable String postId);

    @PostMapping("/api/posts/admin/{postId}/hide")
    void hidePost(@PathVariable String postId);

    @PostMapping("/api/posts/admin/{postId}/unhide")
    void unhidePost(@PathVariable String postId);

    @GetMapping("/api/posts/admin/reported")
    Page<ReportedPostDTO> getReportedPosts(@RequestParam Pageable pageable);
}
