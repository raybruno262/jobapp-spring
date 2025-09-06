package com.MidtermProject.model;

import jakarta.persistence.*;

@Entity

public class JobListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int jobId;
    private String title;
    private String description;
    private String location;
    private Double salary;

    @ManyToOne
    @JoinColumn(name = "jobCategoryId", nullable = false)
    private JobCategory jobCategory;
    

    public JobListing() {
    }

    public JobListing(int jobId) {
        this.jobId = jobId;
    }

    public JobListing(int jobId, String title, String description, String location, Double salary,
            JobCategory jobCategory) {
        this.jobId = jobId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.jobCategory = jobCategory;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public JobCategory getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(JobCategory jobCategory) {
        this.jobCategory = jobCategory;
    }

    

    
    
    

}
