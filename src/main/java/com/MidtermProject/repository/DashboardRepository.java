package com.MidtermProject.repository;
import com.MidtermProject.model.Application;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface DashboardRepository extends JpaRepository<Application, Integer> {

    @Query("SELECT a.status, COUNT(a) FROM Application a GROUP BY a.status")
    List<Object[]> countApplicationsByStatus();


    @Query("SELECT j.title, COUNT(j) FROM JobListing j GROUP BY j.title")
    List<Object[]> countJobsByTitle();

    @Query("SELECT DATE(m.date), COUNT(m) FROM Message m " +
           "WHERE m.date >= :startDate GROUP BY DATE(m.date) ORDER BY DATE(m.date)")
    List<Object[]> countMessagesByDate(LocalDateTime startDate);

    @Query("SELECT DATE(m.date), " +
           "SUM(CASE WHEN m.sender.userId = :userId THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN m.receiver.userId = :userId THEN 1 ELSE 0 END) " +
           "FROM Message m WHERE m.date >= :startDate GROUP BY DATE(m.date) ORDER BY DATE(m.date)")
    List<Object[]> countMessageActivity(int userId, LocalDateTime startDate);

    long countByStatus(String status);


    @Query("SELECT DATE(m.date) as day, COUNT(m) as total, " +
       "SUM(CASE WHEN m.sender.userId IS NOT NULL THEN 1 ELSE 0 END) as sent, " +
       "SUM(CASE WHEN m.receiver.userId IS NOT NULL THEN 1 ELSE 0 END) as received " +
       "FROM Message m WHERE m.date >= :startDate GROUP BY DATE(m.date)")
List<Object[]> countMessageActivity(LocalDateTime startDate);






}