package com.negadras.modumart.notifications;

import java.util.Map;

public record NotificationRequest(
        Long customerId,
        NotificationType type,
        NotificationChannel channel,
        String recipient,
        Map<String, Object> templateData,
        String relatedEntityType,
        Long relatedEntityId
) {
}