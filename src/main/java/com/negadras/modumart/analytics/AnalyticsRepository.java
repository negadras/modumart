package com.negadras.modumart.analytics;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends CrudRepository<AnalyticsMetric, Long> {
    
    List<AnalyticsMetric> findByMetricTypeAndReportDateBetween(String metricType, LocalDate startDate, LocalDate endDate);
    
    List<AnalyticsMetric> findByMetricNameAndReportDateBetween(String metricName, LocalDate startDate, LocalDate endDate);
    
    List<AnalyticsMetric> findByReportDateBetween(LocalDate startDate, LocalDate endDate);
    
    Optional<AnalyticsMetric> findByMetricTypeAndMetricNameAndReportDate(String metricType, String metricName, LocalDate reportDate);
    
    @Query("SELECT SUM(value) FROM analytics_metrics WHERE metric_type = :metricType AND report_date BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> sumByMetricTypeAndDateRange(@Param("metricType") String metricType, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    @Query("SELECT AVG(value) FROM analytics_metrics WHERE metric_type = :metricType AND report_date BETWEEN :startDate AND :endDate")
    Optional<BigDecimal> averageByMetricTypeAndDateRange(@Param("metricType") String metricType,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(*) FROM analytics_metrics WHERE metric_type = :metricType AND report_date = :reportDate")
    Long countByMetricTypeAndDate(@Param("metricType") String metricType, @Param("reportDate") LocalDate reportDate);
    
    @Query("SELECT DISTINCT metric_type FROM analytics_metrics ORDER BY metric_type")
    List<String> findAllMetricTypes();
    
    @Query("SELECT DISTINCT metric_name FROM analytics_metrics WHERE metric_type = :metricType ORDER BY metric_name")
    List<String> findMetricNamesByType(@Param("metricType") String metricType);
}