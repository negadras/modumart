package com.negadras.modumart.payments;

import java.math.BigDecimal;

public record PaymentRequest(
        Long orderId,
        Long customerId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {
}