package com.negadras.modumart.analytics;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    private final AnalyticsRepository analyticsRepository;
    private final BusinessReportRepository businessReportRepository;
    
    public AnalyticsController(AnalyticsService analyticsService,
                             AnalyticsRepository analyticsRepository,
                             BusinessReportRepository businessReportRepository) {
        this.analyticsService = analyticsService;
        this.analyticsRepository = analyticsRepository;
        this.businessReportRepository = businessReportRepository;
    }
    
    // === Dashboard Endpoints ===
    
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetrics> getDashboardMetrics(
            @RequestParam(defaultValue = "DAILY") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LocalDate reportDate = date != null ? date : LocalDate.now();
        DashboardMetrics metrics = analyticsService.generateDashboardMetrics(reportDate, period);
        
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/dashboard/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        LocalDate today = LocalDate.now();
        DashboardMetrics todayMetrics = analyticsService.generateDashboardMetrics(today, "DAILY");
        DashboardMetrics weeklyMetrics = analyticsService.generateDashboardMetrics(today, "WEEKLY");
        DashboardMetrics monthlyMetrics = analyticsService.generateDashboardMetrics(today, "MONTHLY");
        
        Map<String, Object> summary = Map.of(
            "today", todayMetrics,
            "weekly", weeklyMetrics,
            "monthly", monthlyMetrics,
            "lastUpdated", java.time.LocalDateTime.now()
        );
        
        return ResponseEntity.ok(summary);
    }
    
    // === Metrics Endpoints ===
    
    @GetMapping("/metrics")
    public ResponseEntity<List<AnalyticsMetric>> getMetrics(
            @RequestParam(required = false) String metricType,
            @RequestParam(required = false) String metricName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        
        List<AnalyticsMetric> metrics;
        
        if (metricType != null && metricName != null) {
            metrics = analyticsRepository.findByMetricNameAndReportDateBetween(
                metricType + "." + metricName, start, end);
        } else if (metricType != null) {
            metrics = analyticsRepository.findByMetricTypeAndReportDateBetween(metricType, start, end);
        } else {
            metrics = analyticsRepository.findByReportDateBetween(start, end);
        }
        
        return ResponseEntity.ok(metrics);
    }
    
    @GetMapping("/metrics/types")
    public ResponseEntity<List<String>> getMetricTypes() {
        List<String> metricTypes = analyticsRepository.findAllMetricTypes();
        return ResponseEntity.ok(metricTypes);
    }
    
    @GetMapping("/metrics/names")
    public ResponseEntity<List<String>> getMetricNames(@RequestParam String metricType) {
        List<String> metricNames = analyticsRepository.findMetricNamesByType(metricType);
        return ResponseEntity.ok(metricNames);
    }
    
    // === Reports Endpoints ===
    
    @PostMapping("/reports")
    public ResponseEntity<BusinessReport> generateReport(
            @RequestParam String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            BusinessReport report = analyticsService.generateBusinessReport(reportType, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/reports")
    public ResponseEntity<List<BusinessReport>> getReports(
            @RequestParam(required = false) String reportType,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<BusinessReport> reports;
        
        if (reportType != null) {
            reports = businessReportRepository.findByReportTypeOrderByGeneratedAtDesc(reportType);
        } else {
            reports = businessReportRepository.findRecentReports(limit);
        }
        
        return ResponseEntity.ok(reports);
    }
    
    @GetMapping("/reports/{id}")
    public ResponseEntity<BusinessReport> getReport(@PathVariable Long id) {
        return businessReportRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/reports/types")
    public ResponseEntity<List<String>> getReportTypes() {
        List<String> reportTypes = businessReportRepository.findAllReportTypes();
        return ResponseEntity.ok(reportTypes);
    }
    
    // === Export Endpoints ===
    
    @GetMapping("/export/metrics/csv")
    public ResponseEntity<String> exportMetricsCsv(
            @RequestParam(required = false) String metricType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        
        List<AnalyticsMetric> metrics;
        if (metricType != null) {
            metrics = analyticsRepository.findByMetricTypeAndReportDateBetween(metricType, start, end);
        } else {
            metrics = analyticsRepository.findByReportDateBetween(start, end);
        }
        
        StringBuilder csv = new StringBuilder();
        csv.append("ID,MetricType,MetricName,Value,Dimension,ReportDate,CreatedAt\n");
        
        for (AnalyticsMetric metric : metrics) {
            csv.append(String.format("%d,%s,%s,%s,%s,%s,%s\n",
                metric.id(),
                metric.metricType(),
                metric.metricName(), 
                metric.value(),
                metric.dimension(),
                metric.reportDate(),
                metric.createdAt()));
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                   "attachment; filename=analytics-metrics-" + start + "-to-" + end + ".csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(csv.toString());
    }
    
    @GetMapping("/export/dashboard/json")
    public ResponseEntity<DashboardMetrics> exportDashboardJson(
            @RequestParam(defaultValue = "MONTHLY") String period) {
        
        DashboardMetrics metrics = analyticsService.generateDashboardMetrics(LocalDate.now(), period);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                   "attachment; filename=dashboard-" + period.toLowerCase() + "-" + LocalDate.now() + ".json");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(metrics);
    }
    
    // === Real-time Analytics Endpoints ===
    
    @GetMapping("/realtime/summary")
    public ResponseEntity<Map<String, Object>> getRealtimeSummary() {
        LocalDate today = LocalDate.now();
        
        Map<String, Object> summary = Map.of(
            "timestamp", java.time.LocalDateTime.now(),
            "ordersToday", analyticsService.generateDashboardMetrics(today, "DAILY").dailyOrders(),
            "revenueToday", analyticsService.generateDashboardMetrics(today, "DAILY").dailyRevenue(),
            "newCustomersToday", analyticsService.generateDashboardMetrics(today, "DAILY").newCustomers(),
            "systemStatus", "OPERATIONAL"
        );
        
        return ResponseEntity.ok(summary);
    }
    
    // === Analytics Health Check ===
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getAnalyticsHealth() {
        LocalDate today = LocalDate.now();
        Long todayMetricsCount = analyticsRepository.countByMetricTypeAndDate("ALL", today);
        
        Map<String, Object> health = Map.of(
            "status", "UP",
            "metricsToday", todayMetricsCount,
            "lastUpdate", java.time.LocalDateTime.now(),
            "availableReports", businessReportRepository.findAllReportTypes().size()
        );
        
        return ResponseEntity.ok(health);
    }
}