package com.negadras.modumart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EventMonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventMonitoringService.class);
    
    private final AtomicLong totalEventsProcessed = new AtomicLong(0);
    private final AtomicLong eventsInLastHour = new AtomicLong(0);
    private final Map<String, AtomicLong> eventCounters = new ConcurrentHashMap<>();
    
    public EventMonitoringService() {
    }
    
    /**
     * Generic event listener that monitors all application events
     */
    @EventListener
    @Async
    public void handleApplicationEvent(Object event) {
        String eventType = event.getClass().getSimpleName();
        
        // Record metrics
        recordEventMetrics(eventType);
        totalEventsProcessed.incrementAndGet();
        eventsInLastHour.incrementAndGet();
        
        // Log event processing
        logger.debug("Processed event: {} at {}", 
                eventType, 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    /**
     * Record event metrics for monitoring
     */
    public void recordEventMetrics(String eventType) {
        eventCounters.computeIfAbsent(eventType, ignored -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * Scheduled task to log event statistics (Spring Modulith handles cleanup automatically)
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void logEventStatistics() {
        logger.info("=== Event Monitoring Statistics ===");
        logger.info("Total events processed: {}", totalEventsProcessed.get());
        logger.info("Events in last hour: {}", eventsInLastHour.get());
        logger.info("Event types tracked: {}", eventCounters.size());
        
        if (!eventCounters.isEmpty()) {
            logger.info("Event metrics by type:");
            eventCounters.forEach((type, count) -> 
                logger.info("  {}: {} events", type, count.get())
            );
        }
        
        logger.info("Note: Spring Modulith handles event persistence and retry automatically");
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
        return new EventMonitoringStats(
                totalEventsProcessed.get(),
                eventsInLastHour.get(),
                eventCounters.size()
        );
    }
    
    /**
     * Get event metrics by type
     */
    public Map<String, Long> getEventMetrics() {
        return eventCounters.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().get()
                ));
    }
    
    public record EventMonitoringStats(
            long totalEventsProcessed,
            long eventsInLastHour,
            int eventTypes
    ) {}
}