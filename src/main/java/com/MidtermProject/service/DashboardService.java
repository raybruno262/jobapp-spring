package com.MidtermProject.service;

import com.MidtermProject.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final JobListingRepository jobListingRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final HttpSession session;

    public DashboardService(DashboardRepository dashboardRepository,
                          JobListingRepository jobListingRepository,
                          UserRepository userRepository,
                          MessageRepository messageRepository,
                          HttpSession session) {
        this.dashboardRepository = dashboardRepository;
        this.jobListingRepository = jobListingRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.session = session;
    }

    public Map<String, Long> getDashboardSummary() {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) throw new IllegalStateException("User not logged in");

        Map<String, Long> summary = new HashMap<>();
        summary.put("totalJobs", jobListingRepository.count());
   
        summary.put("totalApplications", dashboardRepository.count());
        summary.put("pendingApplications", dashboardRepository.countByStatus("PENDING"));
        summary.put("acceptedApplications", dashboardRepository.countByStatus("ACCEPTED"));
        summary.put("rejectedApplications", dashboardRepository.countByStatus("REJECTED"));
        summary.put("activeUsers", userRepository.count());
   
        summary.put("totalMessages", messageRepository.count());
   

        return summary;
    }



    public List<Object[]> getJobTitles() {
        return dashboardRepository.countJobsByTitle();
    }

    public Map<String, Long> getApplicationStatus() {
        Map<String, Long> statusMap = new HashMap<>();
        List<Object[]> results = dashboardRepository.countApplicationsByStatus();
        for (Object[] result : results) {
            statusMap.put((String) result[0], (Long) result[1]);
        }
        return statusMap;
    }

    public Map<String, Object> getMessageActivity(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Object[]> results = dashboardRepository.countMessageActivity(startDate);
    
        Map<String, Object> activity = new HashMap<>();
        activity.put("dates", results.stream().map(r -> r[0]).toList());
        activity.put("totalMessages", results.stream().map(r -> r[1]).toList());
        activity.put("sentCounts", results.stream().map(r -> r[2]).toList());
        activity.put("receivedCounts", results.stream().map(r -> r[3]).toList());
    
        return activity;
    }


    public Map<String, Object> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();
        
        activities.put("recentMessages", messageRepository.findTop5ByOrderByDateDesc());

        
        return activities;
    }
}