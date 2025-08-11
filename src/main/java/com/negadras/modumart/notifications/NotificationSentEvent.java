package com.negadras.modumart.notifications;

public record NotificationSentEvent(Long notificationId, Long customerId, NotificationChannel channel) {
}