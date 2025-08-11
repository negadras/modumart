package com.negadras.modumart.orders;

public record OrderConfirmedEvent(Long orderId, Long customerId) {
}