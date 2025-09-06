package com.MidtermProject.repository;

import com.MidtermProject.model.Application;
import com.MidtermProject.model.JobListing;
import com.MidtermProject.model.User;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
 boolean existsByUserAndJob(User user, JobListing job);
 List<Application> findByUser(User user);

@Query("SELECT a FROM Application a WHERE LOWER(a.job.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(a.job.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Application> search(@Param("query") String query);

   Page<Application> findByUser(User user, Pageable pageable);
 List<Application> findByJob(JobListing job);
 
 long countByStatus(String status);

 @Transactional
    @Modifying
    @Query("DELETE FROM Application a WHERE a.job = :job")
    void deleteByJob(JobListing job);

}
