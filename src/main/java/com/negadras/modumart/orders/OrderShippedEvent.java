package com.negadras.modumart.orders;

public record OrderShippedEvent(Long orderId, Long customerId, String shippingAddress) {
}