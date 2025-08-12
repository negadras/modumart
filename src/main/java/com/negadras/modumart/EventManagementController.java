package com.negadras.modumart;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventManagementController {
    
    private final EventMonitoringService eventMonitoringService;
    
    public EventManagementController(EventMonitoringService eventMonitoringService) {
        this.eventMonitoringService = eventMonitoringService;
    }
    
    @GetMapping("/monitoring")
    public ResponseEntity<EventMonitoringService.EventMonitoringStats> getMonitoringStats() {
        return ResponseEntity.ok(eventMonitoringService.getMonitoringStats());
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> getEventMetrics() {
        return ResponseEntity.ok(eventMonitoringService.getEventMetrics());
    }
    
    @GetMapping("/info")
    public ResponseEntity<String> getEventInfo() {
        return ResponseEntity.ok("Event persistence and management handled automatically by Spring Modulith. " +
                "Use /monitoring and /metrics endpoints for application-level statistics.");
    }
}