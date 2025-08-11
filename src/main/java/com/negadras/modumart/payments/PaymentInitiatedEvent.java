package com.negadras.modumart.payments;

import java.math.BigDecimal;

public record PaymentInitiatedEvent(Long paymentId, Long orderId, BigDecimal amount) {
}