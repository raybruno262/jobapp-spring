package com.MidtermProject.service;

import com.MidtermProject.repository.ApplicationRepository;
import com.MidtermProject.repository.JobListingRepository;
import com.MidtermProject.repository.UserRepository;
import com.MidtermProject.model.Application;
import com.MidtermProject.model.JobListing;
import com.MidtermProject.model.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JobListingRepository jobListingRepository;
    private final UserRepository userRepository;
    private final HttpSession session;

    // Delete application by ID
    public void deleteApplication(int id) {
        if (!applicationRepository.existsById(id)) {
            throw new EntityNotFoundException("Application with ID " + id + " not found");
        }
        applicationRepository.deleteById(id);
    }

    // Get single application by ID
    public Application getApplicationById(int id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Application with ID " + id + " not found"));
    }

    // Get all applications (non-paginated)
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // Get paginated applications
    public Page<Application> getApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }
// In your ApplicationService
@Transactional
public Application createApplication(Application applicationRequest, int jobId, HttpSession session) {
    // 1. Get user from session
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) throw new IllegalStateException("User not logged in");
    
    // 2. Verify job exists
    JobListing job = jobListingRepository.findById(jobId)
            .orElseThrow(() -> new EntityNotFoundException("Job not found"));
    
    // 3. Verify user exists
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
    
    // 4. Check for duplicate applications
    if (applicationRepository.existsByUserAndJob(user, job)) {
        throw new IllegalStateException("Already applied to this job");
    }
    
    // 5. Create new application
    Application newApp = new Application();
    newApp.setJob(job);
    newApp.setUser(user);
    newApp.setStatus(applicationRequest.getStatus() != null ? 
                   applicationRequest.getStatus() : "PENDING");
    
    // 6. DEBUG: Print before save
    System.out.println("Saving application: " + newApp);
    
    // 7. Save and return
    return applicationRepository.save(newApp);
}
    // Update existing application
    public Application updateApplication(int applicationId, Application applicationDetails) {
        Application existingApplication = getApplicationById(applicationId);

        // Only allow status updates
        if (applicationDetails.getStatus() != null && !applicationDetails.getStatus().isEmpty()) {
            existingApplication.setStatus(applicationDetails.getStatus());
        }

        return applicationRepository.save(existingApplication);
    }

    // Get all applications for current user
    public List<Application> getMyApplications() {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not logged in");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        return applicationRepository.findByUser(user);
    }


   // Get all paginated applications for current user
    public Page<Application> getMyApplications(Pageable pageable) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not logged in");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        return applicationRepository.findByUser(user, pageable);
    }


    public boolean hasUserAppliedToJob(int jobId) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User not logged in");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        JobListing job = jobListingRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job not found"));
        
        return applicationRepository.existsByUserAndJob(user, job);
    }
    // Get all applications for a specific job
    public List<Application> getApplicationsByJob(int jobId) {
        JobListing job = jobListingRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job listing not found"));
        return applicationRepository.findByJob(job);
    }

    // Update just the application status
    public Application updateApplicationStatus(int applicationId, String status) {
        Application application = getApplicationById(applicationId);
        application.setStatus(status);
        return applicationRepository.save(application);
    }
}