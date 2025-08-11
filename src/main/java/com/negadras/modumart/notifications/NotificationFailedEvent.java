package com.negadras.modumart.notifications;

public record NotificationFailedEvent(Long notificationId, Long customerId, String reason) {
}