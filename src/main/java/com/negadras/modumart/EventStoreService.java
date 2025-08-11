package com.negadras.modumart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.core.EventPublicationRegistry;
import org.springframework.modulith.events.core.TargetEventPublication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class EventStoreService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventStoreService.class);
    
    private final EventPublicationRegistry eventPublicationRegistry;
    private final Map<String, AtomicLong> eventCounters = new ConcurrentHashMap<>();
    private final Map<String, Instant> lastEventTime = new ConcurrentHashMap<>();
    
    public EventStoreService(EventPublicationRegistry eventPublicationRegistry) {
        this.eventPublicationRegistry = eventPublicationRegistry;
    }
    
    /**
     * Get statistics about event publications
     */
    public EventStoreStats getEventStoreStats() {
        var incompletePublications = eventPublicationRegistry.findIncompletePublications();
        
        return new EventStoreStats(
                incompletePublications.size(),
                eventCounters.size(),
                getTotalEventCount(),
                getOldestIncompleteEventAge()
        );
    }
    
    /**
     * Clean up completed event publications older than retention period
     */
    public void cleanupCompletedEvents(Duration retentionPeriod) {
        logger.info("Starting cleanup of completed events older than {}", retentionPeriod);
        
        // Note: Spring Modulith handles completed event cleanup automatically
        // This is a placeholder for custom cleanup logic if needed
        logger.info("Event cleanup completed - Spring Modulith handles automatic cleanup");
    }
    
    /**
     * Retry failed event publications
     */
    public int retryFailedEvents() {
        logger.info("Checking failed event publications");
        
        var incompletePublications = eventPublicationRegistry.findIncompletePublications();
        int foundCount = incompletePublications.size();
        
        if (foundCount > 0) {
            logger.info("Found {} incomplete event publications", foundCount);
            // Note: Spring Modulith automatically retries failed events based on configuration
            // This method provides visibility into incomplete events for monitoring purposes
        } else {
            logger.debug("No incomplete event publications found");
        }
        
        return foundCount;
    }
    
    /**
     * Record event metrics for monitoring
     */
    public void recordEventMetrics(String eventType) {
        eventCounters.computeIfAbsent(eventType, ignored -> new AtomicLong(0)).incrementAndGet();
        lastEventTime.put(eventType, Instant.now());
    }
    
    /**
     * Get event metrics by type
     */
    public Map<String, Long> getEventMetrics() {
        return eventCounters.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().get()
                ));
    }
    
    /**
     * Get incomplete event publications
     */
    public List<TargetEventPublication> getIncompleteEvents() {
        Collection<TargetEventPublication> incompletePublications = eventPublicationRegistry.findIncompletePublications();
        return new java.util.ArrayList<>(incompletePublications);
    }
    
    private long getTotalEventCount() {
        return eventCounters.values().stream()
                .mapToLong(AtomicLong::get)
                .sum();
    }
    
    private Duration getOldestIncompleteEventAge() {
        var incompletePublications = eventPublicationRegistry.findIncompletePublications();
        
        return incompletePublications.stream()
                .map(pub -> Duration.between(pub.getPublicationDate(), Instant.now()))
                .max(Duration::compareTo)
                .orElse(Duration.ZERO);
    }
    
    public record EventStoreStats(
            int incompleteEvents,
            int eventTypes,
            long totalEvents,
            Duration oldestIncompleteEventAge
    ) {}
}