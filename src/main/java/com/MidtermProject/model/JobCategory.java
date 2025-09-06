package com.MidtermProject.model;

import jakarta.persistence.*;

@Entity

public class JobCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int jobCategoryId;
    private String categoryName;
    private String description;
    
    public JobCategory() {
    }

    public JobCategory(int jobCategoryId) {
        this.jobCategoryId = jobCategoryId;
    }

    
    public JobCategory(int jobCategoryId, String categoryName, String description) {
        this.jobCategoryId = jobCategoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    public int getJobCategoryId() {
        return jobCategoryId;
    }

    public void setJobCategoryId(int jobCategoryId) {
        this.jobCategoryId = jobCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }





   

    
    

}
