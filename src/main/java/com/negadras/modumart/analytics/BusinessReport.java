package com.negadras.modumart.analytics;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("analytics_reports")
public record BusinessReport(
        @Id Long id,
        String reportType,
        String reportName,
        LocalDate startDate,
        LocalDate endDate,
        String reportData,
        BigDecimal totalRevenue,
        Long totalOrders,
        Long totalCustomers,
        String status,
        LocalDateTime generatedAt,
        String generatedBy
) {
    public BusinessReport withId(Long id) {
        return new BusinessReport(id, reportType, reportName, startDate, endDate, reportData,
                                totalRevenue, totalOrders, totalCustomers, status, generatedAt, generatedBy);
    }
    
    public static BusinessReport create(String reportType, String reportName, LocalDate startDate, 
                                      LocalDate endDate, String reportData, BigDecimal totalRevenue,
                                      Long totalOrders, Long totalCustomers) {
        return new BusinessReport(null, reportType, reportName, startDate, endDate, reportData,
                                totalRevenue, totalOrders, totalCustomers, "COMPLETED", 
                                LocalDateTime.now(), "SYSTEM");
    }
}