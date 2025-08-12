package com.negadras.modumart.analytics;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("analytics_metrics")
public record AnalyticsMetric(
        @Id Long id,
        String metricType,
        String metricName,
        BigDecimal value,
        String dimension,
        LocalDate reportDate,
        LocalDateTime createdAt,
        String metadata
) {
    public AnalyticsMetric withId(Long id) {
        return new AnalyticsMetric(id, metricType, metricName, value, dimension, reportDate, createdAt, metadata);
    }

    public AnalyticsMetric withValue(BigDecimal newValue) {
        return new AnalyticsMetric(id, metricType, metricName, newValue, dimension, reportDate, createdAt, metadata);
    }
    
    public static AnalyticsMetric create(String metricType, String metricName, BigDecimal value, 
                                       String dimension, LocalDate reportDate, String metadata) {
        return new AnalyticsMetric(null, metricType, metricName, value, dimension, reportDate, 
                                 LocalDateTime.now(), metadata);
    }
}