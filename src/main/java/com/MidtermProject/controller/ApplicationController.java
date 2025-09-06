package com.MidtermProject.controller;

import com.MidtermProject.model.Application;

import com.MidtermProject.repository.ApplicationRepository;

import com.MidtermProject.service.ApplicationService;


import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;


    public ApplicationController(ApplicationService applicationService, 
    ApplicationRepository applicationRepository ) {
        this.applicationService = applicationService;


    }
    @PostMapping("/jobs/{jobId}/applications")
public ResponseEntity<?> applyForJob(
        @PathVariable int jobId,
        @RequestBody(required = false) ApplicationRequest request,
        HttpServletRequest httpRequest) {
    
    try {
        // 1. Get session
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(401).body("Login required");
        }
        
        // 2. Prepare application
        Application app = new Application();
        if (request != null) {
            app.setStatus(request.getStatus());
        }
        
        // 3. Save application
        Application savedApp = applicationService.createApplication(app, jobId, session);
        
        // 4. DEBUG: Print after save
        System.out.println("Saved application ID: " + savedApp.getApplicationId());
        
        // 5. Return response
        return ResponseEntity.status(201).body(savedApp);
        
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


// Request DTO
public static class ApplicationRequest {
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

    // Get all applications (paginated)
    @GetMapping("/pagination")
    public ResponseEntity<Page<Application>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
          ) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(applicationService.getApplications(pageable));
    }


  




    // Get application by ID
    @GetMapping("/{id}")
    public ResponseEntity<Application> getApplicationById(@PathVariable int id) {
        try {
            Application application = applicationService.getApplicationById(id);
            return ResponseEntity.ok(application);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update application
    @PutMapping("/{id}")
    public ResponseEntity<Application> updateApplication(
            @PathVariable int id,
            @RequestBody Application applicationDetails) {
        try {
            Application updatedApplication = applicationService.updateApplication(id, applicationDetails);
            return ResponseEntity.ok(updatedApplication);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete application
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable int id) {
        try {
            applicationService.deleteApplication(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get current user's applications (non-paginated)
    @GetMapping("/my-applications")
    public ResponseEntity<List<Application>> getMyApplications() {
        try {
            List<Application> applications = applicationService.getMyApplications();
            return ResponseEntity.ok(applications);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Get current user's applications (paginated)
    @GetMapping("/my-applications/paginated")
    public ResponseEntity<Page<Application>> getMyApplicationsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
          ) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(applicationService.getMyApplications(pageable));
    }


  


    @GetMapping("/has-applied/{jobId}")  // Changed endpoint path
    public ResponseEntity<?> checkIfApplied(
            @PathVariable int jobId,
            HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                   .body("Authentication required");
        }
        
        try {
            boolean hasApplied = applicationService.hasUserAppliedToJob(jobId);
            return ResponseEntity.ok()
                   .body(Collections.singletonMap("hasApplied", hasApplied));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Error checking application status");
        }
    }


    // Get applications by job ID
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJob(@PathVariable int jobId) {
        try {
            List<Application> applications = applicationService.getApplicationsByJob(jobId);
            return ResponseEntity.ok(applications);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update application status
    @PutMapping("/{id}/status")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable int id,
            @RequestParam String status) {
        try {
            Application application = applicationService.updateApplicationStatus(id, status);
            return ResponseEntity.ok(application);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}