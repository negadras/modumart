package com.negadras.modumart.orders;

import java.math.BigDecimal;

public record OrderPaidEvent(Long orderId, Long customerId, BigDecimal amount) {
}