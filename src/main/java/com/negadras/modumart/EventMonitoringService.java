package com.negadras.modumart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EventMonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventMonitoringService.class);
    
    private final EventStoreService eventStoreService;
    private final AtomicLong totalEventsProcessed = new AtomicLong(0);
    private final AtomicLong eventsInLastHour = new AtomicLong(0);
    
    public EventMonitoringService(EventStoreService eventStoreService) {
        this.eventStoreService = eventStoreService;
    }
    
    /**
     * Generic event listener that monitors all application events
     */
    @EventListener
    @Async
    public void handleApplicationEvent(Object event) {
        String eventType = event.getClass().getSimpleName();
        
        // Record metrics
        eventStoreService.recordEventMetrics(eventType);
        totalEventsProcessed.incrementAndGet();
        eventsInLastHour.incrementAndGet();
        
        // Log event processing
        logger.debug("Processed event: {} at {}", 
                eventType, 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    /**
     * Scheduled task to clean up old completed events
     */
    @Scheduled(fixedRateString = "#{@modulithEventProperties.incompleteEventsCleanupInterval.toMillis()}")
    public void cleanupOldEvents() {
        logger.info("Starting scheduled cleanup of old events");
        
        try {
            var properties = eventStoreService.getEventStoreStats();
            logger.info("Event store statistics: {}", properties);
            
            eventStoreService.cleanupCompletedEvents(java.time.Duration.ofDays(7));
            
        } catch (Exception e) {
            logger.error("Error during scheduled event cleanup", e);
        }
    }
    
    /**
     * Scheduled task to retry failed events
     */
    @Scheduled(fixedRateString = "#{@modulithEventProperties.completionUpdateInterval.toMillis()}")
    public void retryFailedEvents() {
        logger.debug("Checking for failed events to retry");
        
        try {
            int retriedCount = eventStoreService.retryFailedEvents();
            
            if (retriedCount > 0) {
                logger.info("Retried {} failed events", retriedCount);
            }
            
        } catch (Exception e) {
            logger.error("Error during failed event retry", e);
        }
    }
    
    /**
     * Scheduled task to log event statistics
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void logEventStatistics() {
        var stats = eventStoreService.getEventStoreStats();
        var metrics = eventStoreService.getEventMetrics();
        
        logger.info("=== Event Store Statistics ===");
        logger.info("Total events processed: {}", totalEventsProcessed.get());
        logger.info("Events in last hour: {}", eventsInLastHour.get());
        logger.info("Incomplete events: {}", stats.incompleteEvents());
        logger.info("Event types tracked: {}", stats.eventTypes());
        logger.info("Oldest incomplete event age: {}", stats.oldestIncompleteEventAge());
        
        if (!metrics.isEmpty()) {
            logger.info("Event metrics by type:");
            metrics.forEach((type, count) -> 
                logger.info("  {}: {} events", type, count)
            );
        }
    }
    
    /**
     * Reset hourly counters
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void resetHourlyCounters() {
        eventsInLastHour.set(0);
        logger.debug("Reset hourly event counters");
    }
    
    /**
     * Get current monitoring statistics
     */
    public EventMonitoringStats getMonitoringStats() {
        var eventStoreStats = eventStoreService.getEventStoreStats();
        
        return new EventMonitoringStats(
                totalEventsProcessed.get(),
                eventsInLastHour.get(),
                eventStoreStats.incompleteEvents(),
                eventStoreStats.eventTypes(),
                eventStoreStats.oldestIncompleteEventAge().toMinutes()
        );
    }
    
    public record EventMonitoringStats(
            long totalEventsProcessed,
            long eventsInLastHour,
            int incompleteEvents,
            int eventTypes,
            long oldestIncompleteEventAgeMinutes
    ) {}
}