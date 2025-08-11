package com.negadras.modumart;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventManagementController {
    
    private final EventStoreService eventStoreService;
    private final EventMonitoringService eventMonitoringService;
    
    public EventManagementController(EventStoreService eventStoreService, 
                                   EventMonitoringService eventMonitoringService) {
        this.eventStoreService = eventStoreService;
        this.eventMonitoringService = eventMonitoringService;
    }
    
    @GetMapping("/stats")
    public ResponseEntity<EventStoreService.EventStoreStats> getEventStoreStats() {
        return ResponseEntity.ok(eventStoreService.getEventStoreStats());
    }
    
    @GetMapping("/monitoring")
    public ResponseEntity<EventMonitoringService.EventMonitoringStats> getMonitoringStats() {
        return ResponseEntity.ok(eventMonitoringService.getMonitoringStats());
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> getEventMetrics() {
        return ResponseEntity.ok(eventStoreService.getEventMetrics());
    }
    
    @GetMapping("/incomplete")
    public ResponseEntity<List<String>> getIncompleteEvents() {
        var incompleteEvents = eventStoreService.getIncompleteEvents();
        var identifiers = incompleteEvents.stream()
                .map(event -> event.getIdentifier().toString())
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(identifiers);
    }
    
    @PostMapping("/retry-failed")
    public ResponseEntity<RetryResult> retryFailedEvents() {
        int incompleteCount = eventStoreService.retryFailedEvents();
        return ResponseEntity.ok(new RetryResult(incompleteCount, "Found " + incompleteCount + " incomplete events. Spring Modulith handles automatic retry."));
    }
    
    @PostMapping("/cleanup")
    public ResponseEntity<String> cleanupCompletedEvents() {
        eventStoreService.cleanupCompletedEvents(java.time.Duration.ofDays(7));
        return ResponseEntity.ok("Completed events cleanup initiated");
    }
    
    public record RetryResult(int count, String message) {}
}