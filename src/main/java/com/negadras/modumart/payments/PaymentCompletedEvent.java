package com.negadras.modumart.payments;

import java.math.BigDecimal;

public record PaymentCompletedEvent(Long paymentId, Long orderId, Long customerId, BigDecimal amount) {
}