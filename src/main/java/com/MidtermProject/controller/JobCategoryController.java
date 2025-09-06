package com.MidtermProject.controller;

import com.MidtermProject.model.JobCategory;
import com.MidtermProject.service.JobCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
    
@RestController
@RequestMapping("/api/jobcategories")
public class JobCategoryController {

    private final JobCategoryService jobCategoryService;

    public JobCategoryController(JobCategoryService jobCategoryService) {
        this.jobCategoryService = jobCategoryService;
    }

    // Create a new job category

    @PostMapping
    public ResponseEntity<JobCategory> createJobCategory(@RequestBody JobCategory jobCategory) {
        JobCategory savedCategory = jobCategoryService.saveJobCategory(jobCategory);
        return ResponseEntity.ok(savedCategory);
    }


    // Get all job categories (unpaginated)
    @GetMapping
    public ResponseEntity<List<JobCategory>> getAllJobCategories() {
        List<JobCategory> categories = jobCategoryService.getAllJobCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Get paginated job categories
    @GetMapping("/paginated")
    public ResponseEntity<Page<JobCategory>> getPaginatedJobCategories(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobCategory> categories = jobCategoryService.getJobCategories(pageable);
        return ResponseEntity.ok(categories);
    }









    
    // Get a single job category by ID
    @GetMapping("/{id}")
    public ResponseEntity<JobCategory> getJobCategoryById(@PathVariable int id) {
        JobCategory category = jobCategoryService.getJobCategoryById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    // Update a job category
    @PutMapping("/{id}")
    public ResponseEntity<JobCategory> updateJobCategory(
            @PathVariable int id, 
            @RequestBody JobCategory jobCategory) {
        JobCategory updatedCategory = jobCategoryService.updateJobCategory(id, jobCategory);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // Delete a job category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobCategory(@PathVariable int id) {
        jobCategoryService.deleteJobCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
} 