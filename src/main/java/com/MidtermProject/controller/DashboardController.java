package com.MidtermProject.controller;

import com.MidtermProject.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Long>> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }


    @GetMapping("/job-categories")
    public ResponseEntity<List<Object[]>> getJobCategories() {
        return ResponseEntity.ok(dashboardService.getJobTitles());
    }

    @GetMapping("/application-status")
    public ResponseEntity<Map<String, Long>> getApplicationStatus() {
        return ResponseEntity.ok(dashboardService.getApplicationStatus());
    }

    @GetMapping("/message-activity")
    public ResponseEntity<Map<String, Object>> getMessageActivity(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(dashboardService.getMessageActivity(days));
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<Map<String, Object>> getRecentActivities() {
        return ResponseEntity.ok(dashboardService.getRecentActivities());
    }
}