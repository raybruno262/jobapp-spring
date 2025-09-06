package com.MidtermProject.model;



import jakarta.persistence.*;

@Entity

public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int applicationId;

    @ManyToOne
    @JoinColumn(name = "jobId", nullable = false)
    private JobListing job;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;


    @Column(nullable = false)
    private String status;


    public Application() {
    }


    public Application(int applicationId) {
        this.applicationId = applicationId;
    }


    public int getApplicationId() {
        return applicationId;
    }


    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }


    public JobListing getJob() {
        return job;
    }


    public void setJob(JobListing job2) {
        this.job = job2;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }

}
