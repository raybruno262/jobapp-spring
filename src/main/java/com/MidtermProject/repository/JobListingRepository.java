package com.MidtermProject.repository;

import com.MidtermProject.model.JobListing;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface JobListingRepository extends JpaRepository<JobListing, Integer> {

     @Query("SELECT j FROM JobListing j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<JobListing> search(@Param("query") String query);



    

}
