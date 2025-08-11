package com.negadras.modumart.payments;

public record PaymentFailedEvent(Long paymentId, Long orderId, String reason) {
}