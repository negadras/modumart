package com.negadras.modumart.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record DashboardMetrics(
        // Sales Metrics
        BigDecimal totalRevenue,
        BigDecimal dailyRevenue,
        BigDecimal monthlyRevenue,
        Long totalOrders,
        Long dailyOrders,
        BigDecimal averageOrderValue,
        
        // Customer Metrics  
        Long totalCustomers,
        Long newCustomers,
        Long activeCustomers,
        BigDecimal customerLifetimeValue,
        
        // Product Metrics
        Long totalProducts,
        Long lowStockProducts,
        Long outOfStockProducts,
        String topSellingProduct,
        
        // Fulfillment Metrics
        Long pendingOrders,
        Long shippedOrders,
        Long deliveredOrders,
        Double averageShippingTime,
        
        // System Metrics
        Long totalNotifications,
        Long failedNotifications,
        Double notificationSuccessRate,
        
        // Metadata
        LocalDate reportDate,
        String reportPeriod,
        Map<String, Object> additionalMetrics
) {
    
    public static DashboardMetrics empty() {
        return new DashboardMetrics(
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
            0L, 0L, BigDecimal.ZERO,
            0L, 0L, 0L, BigDecimal.ZERO,
            0L, 0L, 0L, "None",
            0L, 0L, 0L, 0.0,
            0L, 0L, 0.0,
            LocalDate.now(), "DAILY", Map.of()
        );
    }
}