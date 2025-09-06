package com.MidtermProject.service;

import com.MidtermProject.repository.ApplicationRepository;
import com.MidtermProject.repository.JobCategoryRepository;
import com.MidtermProject.repository.JobListingRepository;

import com.MidtermProject.model.JobCategory;
import com.MidtermProject.model.JobListing;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobListingService {
    private final JobListingRepository jobListingRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final ApplicationRepository applicationRepository;





    
// JobListingService.java
@Transactional
public void deleteJobListing(int id) {
    JobListing job = jobListingRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Job Listing not found with ID: " + id));
    
    // First delete any applications associated with this job
    applicationRepository.deleteByJob(job);
    
    // Then delete the job listing
    jobListingRepository.delete(job);
}

    // Method to get a job listing by ID
    public JobListing getJobListingById(int id) {
        return jobListingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job Listing not found with id: " + id));
    }

    // Method to get all non-paginated job listings
    public List<JobListing> getAllJobListings() {
        return jobListingRepository.findAll();
    }

    // Method to get paginated job listings

    public Page<JobListing> getJobListings(Pageable pageable) {
        return jobListingRepository.findAll(pageable);
    }



    // Method to create a new job listing
    public JobListing createJobListing(JobListing jobListing) {
        // Validate that the job category exists
        Optional<JobCategory> optionalJobCategory = jobCategoryRepository.findById(jobListing.getJobCategory().getJobCategoryId());
        if (optionalJobCategory.isEmpty()) {
            throw new EntityNotFoundException("Job Category not found with id: " + jobListing.getJobCategory().getJobCategoryId());
        }
        
        // Set the job category from the database
        jobListing.setJobCategory(optionalJobCategory.get());
        
        // Save and return the job listing
        return jobListingRepository.save(jobListing);
    }

    // Method to update a job listing
    public JobListing updateJobListing(int jobId, JobListing jobListing) {
        JobListing existingJobListing = jobListingRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Job Listing not found with id: " + jobId));
                
        // Validate that the new job category exists
        Optional<JobCategory> optionalJobCategory = jobCategoryRepository.findById(jobListing.getJobCategory().getJobCategoryId());
        if (optionalJobCategory.isEmpty()) {
            throw new EntityNotFoundException("Job Category not found with id: " + jobListing.getJobCategory().getJobCategoryId());
        }
        
        // Update the existing job listing with new values
        existingJobListing.setTitle(jobListing.getTitle());
        existingJobListing.setDescription(jobListing.getDescription());
        existingJobListing.setLocation(jobListing.getLocation());
        existingJobListing.setSalary(jobListing.getSalary());
        existingJobListing.setJobCategory(optionalJobCategory.get());
        
        // Save and return the updated job listing
        return jobListingRepository.save(existingJobListing);
    }

}