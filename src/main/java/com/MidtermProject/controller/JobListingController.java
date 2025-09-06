package com.MidtermProject.controller;

import com.MidtermProject.service.JobListingService;import com.MidtermProject.model.JobListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-listings")
public class JobListingController {

    private final JobListingService jobListingService;

    public JobListingController(JobListingService jobListingService) {
        this.jobListingService = jobListingService;
    }

    // Create a new job listing
    @PostMapping
    public ResponseEntity<JobListing> createJobListing(@RequestBody JobListing jobListing) {
        JobListing createdJobListing = jobListingService.createJobListing(jobListing);
        return new ResponseEntity<>(createdJobListing, HttpStatus.CREATED);
    }

    // Get all job listings (non-paginated)
    @GetMapping
    public ResponseEntity<List<JobListing>> getAllJobListings() {
        List<JobListing> jobListings = jobListingService.getAllJobListings();
        return new ResponseEntity<>(jobListings, HttpStatus.OK);
    }

    // Get paginated job listings


        @GetMapping("/paginated")
    public ResponseEntity<Page<JobListing>> getPaginatedJobListings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobListing> x = jobListingService.getJobListings(pageable);
        return ResponseEntity.ok(x);
    }




    // Get a single job listing by ID
    @GetMapping("/{id}")
    public ResponseEntity<JobListing> getJobListingById(@PathVariable int id) {
        JobListing jobListing = jobListingService.getJobListingById(id);
        return new ResponseEntity<>(jobListing, HttpStatus.OK);
    }

    // Update a job listing
    @PutMapping("/{id}")
    public ResponseEntity<JobListing> updateJobListing(
            @PathVariable int id,
            @RequestBody JobListing jobListing) {
        JobListing updatedJobListing = jobListingService.updateJobListing(id, jobListing);
        return new ResponseEntity<>(updatedJobListing, HttpStatus.OK);
    }

    // Delete a job listing
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobListing(@PathVariable int id) {
        jobListingService.deleteJobListing(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}