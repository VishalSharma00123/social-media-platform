package com.admin_service.admin_service.service;

// ReportService.java

import com.admin_service.admin_service.model.Report;
import com.admin_service.admin_service.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;

    public Report createReport(Report report) {
        report.setCreatedAt(LocalDateTime.now());
        report.setStatus(Report.ReportStatus.PENDING);
        return reportRepository.save(report);
    }

    public Page<Report> getReports(Report.ReportStatus status, Pageable pageable) {
        if (status != null) {
            return reportRepository.findByStatus(status, pageable);
        }
        return reportRepository.findAll(pageable);
    }

    public Report getReportById(String reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public Report reviewReport(String reportId, String adminId, String action, String notes) {
        Report report = getReportById(reportId);

        report.setReviewedBy(adminId);
        report.setReviewedAt(LocalDateTime.now());
        report.setReviewNotes(notes);
        report.setActionTaken(action);

        switch (action.toUpperCase()) {
            case "RESOLVE":
                report.setStatus(Report.ReportStatus.RESOLVED);
                break;
            case "DISMISS":
                report.setStatus(Report.ReportStatus.DISMISSED);
                break;
            case "UNDER_REVIEW":
                report.setStatus(Report.ReportStatus.UNDER_REVIEW);
                break;
            default:
                throw new RuntimeException("Invalid action: " + action);
        }

        return reportRepository.save(report);
    }
}
