package com.negadras.modumart;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.config.EnablePersistentDomainEvents;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

@Configuration
@EnablePersistentDomainEvents
@EnableAsync
@EnableScheduling
public class ModulithConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.modulith.events")
    public ModulithEventProperties modulithEventProperties() {
        return new ModulithEventProperties();
    }

    public static class ModulithEventProperties {
        private Duration completionUpdateInterval = Duration.ofMinutes(1);
        private Duration incompleteEventsCleanupInterval = Duration.ofHours(1);
        private Duration retentionDuration = Duration.ofDays(7);

        public Duration getCompletionUpdateInterval() {
            return completionUpdateInterval;
        }

        public void setCompletionUpdateInterval(Duration completionUpdateInterval) {
            this.completionUpdateInterval = completionUpdateInterval;
        }

        public Duration getIncompleteEventsCleanupInterval() {
            return incompleteEventsCleanupInterval;
        }

        public void setIncompleteEventsCleanupInterval(Duration incompleteEventsCleanupInterval) {
            this.incompleteEventsCleanupInterval = incompleteEventsCleanupInterval;
        }

        public Duration getRetentionDuration() {
            return retentionDuration;
        }

        public void setRetentionDuration(Duration retentionDuration) {
            this.retentionDuration = retentionDuration;
        }
    }
}
