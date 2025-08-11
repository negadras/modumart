package com.negadras.modumart.orders;

public record OrderDeliveredEvent(Long orderId, Long customerId) {
}