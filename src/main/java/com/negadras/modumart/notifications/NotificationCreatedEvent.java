package com.negadras.modumart.notifications;

public record NotificationCreatedEvent(Long notificationId, Long customerId, NotificationType type) {
}