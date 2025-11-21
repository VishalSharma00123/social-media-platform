package com.admin_service.admin_service.controller;

import com.admin_service.admin_service.model.Report;
import com.admin_service.admin_service.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
public class ReportsController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<Page<Report>> getReports(
            @RequestParam(required = false) Report.ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reportService.getReports(status, pageable));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<Report> getReport(@PathVariable String reportId) {
        return ResponseEntity.ok(reportService.getReportById(reportId));
    }

    @PostMapping("/{reportId}/review")
    public ResponseEntity<Report> reviewReport(
            @PathVariable String reportId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal String adminId) {
        String action = request.get("action");
        String notes = request.get("notes");
        return ResponseEntity.ok(reportService.reviewReport(reportId, adminId, action, notes));
    }
}