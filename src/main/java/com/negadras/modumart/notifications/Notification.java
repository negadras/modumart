package com.negadras.modumart.notifications;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("notifications")
public record Notification(
        @Id Long id,
        Long customerId,
        NotificationType type,
        NotificationChannel channel,
        String title,
        String message,
        String recipient,
        NotificationStatus status,
        LocalDateTime createdAt,
        LocalDateTime sentAt,
        String relatedEntityType,
        Long relatedEntityId,
        String metadata
) {
    public Notification withId(Long id) {
        return new Notification(id, customerId, type, channel, title, message, recipient, status, createdAt, sentAt, relatedEntityType, relatedEntityId, metadata);
    }
    
    public Notification withStatus(NotificationStatus status) {
        return new Notification(id, customerId, type, channel, title, message, recipient, status, createdAt, sentAt, relatedEntityType, relatedEntityId, metadata);
    }
    
    public Notification withSentAt(LocalDateTime sentAt) {
        return new Notification(id, customerId, type, channel, title, message, recipient, NotificationStatus.SENT, createdAt, sentAt, relatedEntityType, relatedEntityId, metadata);
    }
    
    public static Notification create(Long customerId, NotificationType type, NotificationChannel channel, 
                                    String title, String message, String recipient,
                                    String relatedEntityType, Long relatedEntityId) {
        return new Notification(
                null, customerId, type, channel, title, message, recipient,
                NotificationStatus.PENDING, LocalDateTime.now(), null,
                relatedEntityType, relatedEntityId, null
        );
    }
}