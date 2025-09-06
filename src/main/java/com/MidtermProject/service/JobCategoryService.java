package com.MidtermProject.service;



import com.MidtermProject.model.JobCategory;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Pageable;
import com.MidtermProject.repository.JobCategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

@Service
public class JobCategoryService {
    private final JobCategoryRepository jobCategoryRepository;

    // Constructor with dependency injection
    public JobCategoryService(JobCategoryRepository jobCategoryRepository) {
        this.jobCategoryRepository = jobCategoryRepository;
    }

    // Update a job category
    public JobCategory updateJobCategory(int id, JobCategory jobCategory) {
        Optional<JobCategory> optionalJobCategory = jobCategoryRepository.findById(id);
        if (optionalJobCategory.isPresent()) {
            JobCategory category = optionalJobCategory.get();
            category.setCategoryName(jobCategory.getCategoryName());
            category.setDescription(jobCategory.getDescription());
            return jobCategoryRepository.save(category);
        } else {
            throw new EntityNotFoundException("Job category not updated");
        }
    }

    // Save a job category
    public JobCategory saveJobCategory(JobCategory jobCategory) {
        return jobCategoryRepository.save(jobCategory);
    }

    // Delete a job category
    public void deleteJobCategory(int id) {
        Optional<JobCategory> optionalJobCategory = jobCategoryRepository.findById(id);
        if (optionalJobCategory.isPresent()) {
            jobCategoryRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Job category not deleted");
        }
    }

    // Get job category by ID
    public JobCategory getJobCategoryById(int id) {
        Optional<JobCategory> optionalJobCategory = jobCategoryRepository.findById(id);
        if (optionalJobCategory.isPresent()) {
            return optionalJobCategory.get();
        } else {
            throw new EntityNotFoundException("Job category not found");
        }
    }

    // Get all job categories
    public List<JobCategory> getAllJobCategories() {
        return jobCategoryRepository.findAll().stream().collect(Collectors.toList());
    }

    // Get paginated job categories
    public Page<JobCategory> getJobCategories(Pageable pageable) {
        return jobCategoryRepository.findAll(pageable);
    }

    
}
