package com.negadras.modumart.orders;

public record OrderCancelledEvent(Long orderId, Long customerId) {
}