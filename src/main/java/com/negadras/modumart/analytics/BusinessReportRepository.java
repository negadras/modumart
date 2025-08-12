package com.negadras.modumart.analytics;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BusinessReportRepository extends CrudRepository<BusinessReport, Long> {
    
    List<BusinessReport> findByReportTypeAndStartDateBetween(String reportType, LocalDate startDate, LocalDate endDate);
    
    List<BusinessReport> findByReportTypeOrderByGeneratedAtDesc(String reportType);
    
    List<BusinessReport> findByGeneratedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<BusinessReport> findByStatus(String status);
    
    @Query("SELECT * FROM analytics_reports WHERE start_date <= :date AND end_date >= :date ORDER BY generated_at DESC")
    List<BusinessReport> findReportsForDate(@Param("date") LocalDate date);
    
    @Query("SELECT * FROM analytics_reports ORDER BY generated_at DESC LIMIT :limit")
    List<BusinessReport> findRecentReports(@Param("limit") int limit);
    
    @Query("SELECT DISTINCT report_type FROM analytics_reports ORDER BY report_type")
    List<String> findAllReportTypes();
}