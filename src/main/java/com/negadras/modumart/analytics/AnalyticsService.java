package com.negadras.modumart.analytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    
    private final AnalyticsRepository analyticsRepository;
    private final BusinessReportRepository businessReportRepository;
    
    public AnalyticsService(AnalyticsRepository analyticsRepository, 
                          BusinessReportRepository businessReportRepository) {
        this.analyticsRepository = analyticsRepository;
        this.businessReportRepository = businessReportRepository;
    }
    
    // === Event Recording Methods ===
    
    public void recordOrderCreated(Long orderId, Long customerId) {
        recordMetric("ORDERS", "orders_created", BigDecimal.ONE, "daily", 
                    Map.of("orderId", orderId, "customerId", customerId));
    }
    
    public void recordOrderConfirmed(Long orderId, Long customerId) {
        recordMetric("ORDERS", "orders_confirmed", BigDecimal.ONE, "daily",
                    Map.of("orderId", orderId, "customerId", customerId));
    }
    
    public void recordOrderPaid(Long orderId, Long customerId, BigDecimal amount) {
        recordMetric("REVENUE", "order_revenue", amount, "daily",
                    Map.of("orderId", orderId, "customerId", customerId));
        recordMetric("ORDERS", "orders_paid", BigDecimal.ONE, "daily",
                    Map.of("orderId", orderId, "customerId", customerId));
    }
    
    public void recordOrderShipped(Long orderId, Long customerId) {
        recordMetric("FULFILLMENT", "orders_shipped", BigDecimal.ONE, "daily",
                    Map.of("orderId", orderId, "customerId", customerId));
    }
    
    public void recordOrderDelivered(Long orderId, Long customerId) {
        recordMetric("FULFILLMENT", "orders_delivered", BigDecimal.ONE, "daily",
                    Map.of("orderId", orderId, "customerId", customerId));
    }
    
    public void recordOrderCancelled(Long orderId, Long customerId) {
        recordMetric("ORDERS", "orders_cancelled", BigDecimal.ONE, "daily",
                    Map.of("orderId", orderId, "customerId", customerId));
    }
    
    public void recordCustomerRegistered(Long customerId, String email) {
        recordMetric("CUSTOMERS", "customers_registered", BigDecimal.ONE, "daily",
                    Map.of("customerId", customerId, "email", email));
    }
    
    public void recordProductCreated(Long productId, String category) {
        recordMetric("CATALOG", "products_created", BigDecimal.ONE, "daily",
                    Map.of("productId", productId, "category", category));
    }
    
    public void recordProductStockReduced(Long productId, Integer quantityReduced) {
        recordMetric("INVENTORY", "stock_reduced", BigDecimal.valueOf(quantityReduced), "daily",
                    Map.of("productId", productId));
    }
    
    public void recordProductOutOfStock(Long productId) {
        recordMetric("INVENTORY", "out_of_stock", BigDecimal.ONE, "daily",
                    Map.of("productId", productId));
    }
    
    public void recordPaymentCompleted(Long paymentId, Long orderId, BigDecimal amount) {
        recordMetric("PAYMENTS", "payments_completed", BigDecimal.ONE, "daily",
                    Map.of("paymentId", paymentId, "orderId", orderId));
        recordMetric("REVENUE", "payment_revenue", amount, "daily",
                    Map.of("paymentId", paymentId, "orderId", orderId));
    }
    
    public void recordPaymentFailed(Long paymentId, Long orderId, String reason) {
        recordMetric("PAYMENTS", "payments_failed", BigDecimal.ONE, "daily",
                    Map.of("paymentId", paymentId, "orderId", orderId, "reason", reason));
    }
    
    public void recordPaymentRefunded(Long paymentId, Long orderId, BigDecimal amount) {
        recordMetric("PAYMENTS", "payments_refunded", BigDecimal.ONE, "daily",
                    Map.of("paymentId", paymentId, "orderId", orderId));
        recordMetric("REVENUE", "refund_amount", amount.negate(), "daily",
                    Map.of("paymentId", paymentId, "orderId", orderId));
    }
    
    public void recordShipmentCreated(Long shipmentId, Long orderId) {
        recordMetric("SHIPPING", "shipments_created", BigDecimal.ONE, "daily",
                    Map.of("shipmentId", shipmentId, "orderId", orderId));
    }
    
    public void recordShipmentDelivered(Long shipmentId, Long orderId) {
        recordMetric("SHIPPING", "shipments_delivered", BigDecimal.ONE, "daily",
                    Map.of("shipmentId", shipmentId, "orderId", orderId));
    }
    
    public void recordShipmentDeliveryFailed(Long shipmentId, Long orderId, String reason) {
        recordMetric("SHIPPING", "delivery_failed", BigDecimal.ONE, "daily",
                    Map.of("shipmentId", shipmentId, "orderId", orderId, "reason", reason));
    }
    
    public void recordNotificationSent(Long notificationId, Long customerId, String type) {
        recordMetric("NOTIFICATIONS", "notifications_sent", BigDecimal.ONE, "daily",
                    Map.of("notificationId", notificationId, "customerId", customerId, "type", type));
    }
    
    public void recordNotificationFailed(Long notificationId, Long customerId, String reason) {
        recordMetric("NOTIFICATIONS", "notifications_failed", BigDecimal.ONE, "daily",
                    Map.of("notificationId", notificationId, "customerId", customerId, "reason", reason));
    }
    
    // === Core Metric Recording ===
    
    private void recordMetric(String metricType, String metricName, BigDecimal value, 
                            String dimension, Map<String, Object> metadata) {
        try {
            LocalDate today = LocalDate.now();
            String metadataJson = convertMetadataToJson(metadata);
            
            // Check if metric already exists for today and update, otherwise create new
            var existingMetric = analyticsRepository
                .findByMetricTypeAndMetricNameAndReportDate(metricType, metricName, today);
                
            if (existingMetric.isPresent()) {
                var updated = existingMetric.get().withValue(existingMetric.get().value().add(value));
                analyticsRepository.save(updated);
                logger.debug("Updated metric: {} = {}", metricName, updated.value());
            } else {
                var newMetric = AnalyticsMetric.create(metricType, metricName, value, dimension, today, metadataJson);
                analyticsRepository.save(newMetric);
                logger.debug("Created new metric: {} = {}", metricName, value);
            }
        } catch (Exception e) {
            logger.error("Failed to record metric {}.{}: {}", metricType, metricName, e.getMessage());
        }
    }
    
    // === Dashboard Metrics Generation ===
    
    public DashboardMetrics generateDashboardMetrics(LocalDate reportDate, String period) {
        try {
            LocalDate startDate = calculateStartDate(reportDate, period);
            LocalDate endDate = reportDate;
            
            return new DashboardMetrics(
                // Sales Metrics
                getTotalRevenue(startDate, endDate),
                getDailyRevenue(reportDate),
                getMonthlyRevenue(reportDate),
                getTotalOrders(startDate, endDate),
                getDailyOrders(reportDate), 
                getAverageOrderValue(startDate, endDate),
                
                // Customer Metrics
                getTotalCustomers(startDate, endDate),
                getNewCustomers(startDate, endDate),
                getActiveCustomers(startDate, endDate),
                getCustomerLifetimeValue(),
                
                // Product Metrics
                getTotalProducts(),
                getLowStockProducts(),
                getOutOfStockProducts(),
                getTopSellingProduct(startDate, endDate),
                
                // Fulfillment Metrics
                getPendingOrders(),
                getShippedOrders(startDate, endDate),
                getDeliveredOrders(startDate, endDate),
                getAverageShippingTime(),
                
                // System Metrics
                getTotalNotifications(startDate, endDate),
                getFailedNotifications(startDate, endDate),
                getNotificationSuccessRate(startDate, endDate),
                
                // Metadata
                reportDate,
                period,
                getAdditionalMetrics(startDate, endDate)
            );
        } catch (Exception e) {
            logger.error("Failed to generate dashboard metrics: {}", e.getMessage());
            return DashboardMetrics.empty();
        }
    }
    
    // === Business Report Generation ===
    
    public BusinessReport generateBusinessReport(String reportType, LocalDate startDate, LocalDate endDate) {
        try {
            String reportName = String.format("%s Report - %s to %s", 
                reportType, startDate.format(DateTimeFormatter.ISO_DATE), 
                endDate.format(DateTimeFormatter.ISO_DATE));
                
            Map<String, Object> reportData = generateReportData(reportType, startDate, endDate);
            String reportDataJson = convertMetadataToJson(reportData);
            
            BigDecimal totalRevenue = getTotalRevenue(startDate, endDate);
            Long totalOrders = getTotalOrders(startDate, endDate);
            Long totalCustomers = getTotalCustomers(startDate, endDate);
            
            var report = BusinessReport.create(reportType, reportName, startDate, endDate, 
                                             reportDataJson, totalRevenue, totalOrders, totalCustomers);
            
            return businessReportRepository.save(report);
        } catch (Exception e) {
            logger.error("Failed to generate business report: {}", e.getMessage());
            throw new RuntimeException("Report generation failed", e);
        }
    }
    
    // === Helper Methods ===
    
    private LocalDate calculateStartDate(LocalDate endDate, String period) {
        return switch (period.toUpperCase()) {
            case "DAILY" -> endDate;
            case "WEEKLY" -> endDate.minusWeeks(1);
            case "MONTHLY" -> endDate.minusMonths(1);
            case "QUARTERLY" -> endDate.minusMonths(3);
            case "YEARLY" -> endDate.minusYears(1);
            default -> endDate.minusDays(7);
        };
    }
    
    private BigDecimal getMetricSum(String metricType, String metricName, LocalDate startDate, LocalDate endDate) {
        return analyticsRepository.sumByMetricTypeAndDateRange(metricType + "." + metricName, startDate, endDate)
                .orElse(BigDecimal.ZERO);
    }
    
    private BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        return getMetricSum("REVENUE", "order_revenue", startDate, endDate)
               .add(getMetricSum("REVENUE", "payment_revenue", startDate, endDate))
               .add(getMetricSum("REVENUE", "refund_amount", startDate, endDate));
    }
    
    private BigDecimal getDailyRevenue(LocalDate date) {
        return getTotalRevenue(date, date);
    }
    
    private BigDecimal getMonthlyRevenue(LocalDate date) {
        LocalDate startOfMonth = date.withDayOfMonth(1);
        return getTotalRevenue(startOfMonth, date);
    }
    
    private Long getTotalOrders(LocalDate startDate, LocalDate endDate) {
        return getMetricSum("ORDERS", "orders_created", startDate, endDate).longValue();
    }
    
    private Long getDailyOrders(LocalDate date) {
        return getTotalOrders(date, date);
    }
    
    private BigDecimal getAverageOrderValue(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalRevenue = getTotalRevenue(startDate, endDate);
        Long totalOrders = getTotalOrders(startDate, endDate);
        
        if (totalOrders == 0) return BigDecimal.ZERO;
        return totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);
    }
    
    private Long getTotalCustomers(LocalDate startDate, LocalDate endDate) {
        return getMetricSum("CUSTOMERS", "customers_registered", startDate, endDate).longValue();
    }
    
    private Long getNewCustomers(LocalDate startDate, LocalDate endDate) {
        return getTotalCustomers(startDate, endDate);
    }
    
    private Long getActiveCustomers(LocalDate startDate, LocalDate endDate) {
        // Simplified: customers who placed orders
        return getMetricSum("ORDERS", "orders_created", startDate, endDate).longValue();
    }
    
    private BigDecimal getCustomerLifetimeValue() {
        // Simplified calculation
        return BigDecimal.valueOf(500.00);
    }
    
    private Long getTotalProducts() {
        return getMetricSum("CATALOG", "products_created", LocalDate.now().minusYears(1), LocalDate.now()).longValue();
    }
    
    private Long getLowStockProducts() {
        // Simplified: products with stock reduction events
        return getMetricSum("INVENTORY", "stock_reduced", LocalDate.now().minusDays(7), LocalDate.now()).longValue();
    }
    
    private Long getOutOfStockProducts() {
        return getMetricSum("INVENTORY", "out_of_stock", LocalDate.now().minusDays(7), LocalDate.now()).longValue();
    }
    
    private String getTopSellingProduct(LocalDate startDate, LocalDate endDate) {
        return "Product #1"; // Simplified
    }
    
    private Long getPendingOrders() {
        return getMetricSum("ORDERS", "orders_created", LocalDate.now().minusDays(7), LocalDate.now()).longValue()
               - getMetricSum("ORDERS", "orders_shipped", LocalDate.now().minusDays(7), LocalDate.now()).longValue();
    }
    
    private Long getShippedOrders(LocalDate startDate, LocalDate endDate) {
        return getMetricSum("FULFILLMENT", "orders_shipped", startDate, endDate).longValue();
    }
    
    private Long getDeliveredOrders(LocalDate startDate, LocalDate endDate) {
        return getMetricSum("FULFILLMENT", "orders_delivered", startDate, endDate).longValue();
    }
    
    private Double getAverageShippingTime() {
        return 2.5; // Simplified: 2.5 days average
    }
    
    private Long getTotalNotifications(LocalDate startDate, LocalDate endDate) {
        return getMetricSum("NOTIFICATIONS", "notifications_sent", startDate, endDate).longValue();
    }
    
    private Long getFailedNotifications(LocalDate startDate, LocalDate endDate) {
        return getMetricSum("NOTIFICATIONS", "notifications_failed", startDate, endDate).longValue();
    }
    
    private Double getNotificationSuccessRate(LocalDate startDate, LocalDate endDate) {
        Long total = getTotalNotifications(startDate, endDate);
        Long failed = getFailedNotifications(startDate, endDate);
        
        if (total == 0) return 100.0;
        return ((double)(total - failed) / total) * 100.0;
    }
    
    private Map<String, Object> getAdditionalMetrics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("reportGenerated", java.time.LocalDateTime.now());
        metrics.put("dataPoints", analyticsRepository.countByMetricTypeAndDate("ALL", LocalDate.now()));
        metrics.put("version", "1.0");
        return metrics;
    }
    
    private Map<String, Object> generateReportData(String reportType, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> data = new HashMap<>();
        data.put("reportType", reportType);
        data.put("period", startDate + " to " + endDate);
        data.put("totalRevenue", getTotalRevenue(startDate, endDate));
        data.put("totalOrders", getTotalOrders(startDate, endDate));
        data.put("totalCustomers", getTotalCustomers(startDate, endDate));
        data.put("generatedAt", java.time.LocalDateTime.now());
        return data;
    }
    
    private String convertMetadataToJson(Map<String, Object> metadata) {
        // Simplified JSON conversion - in production use Jackson or similar
        return metadata.entrySet().stream()
            .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
            .collect(Collectors.joining(",", "{", "}"));
    }
}