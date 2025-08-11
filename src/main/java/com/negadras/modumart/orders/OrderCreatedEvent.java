package com.negadras.modumart.orders;

import java.util.Set;

public record OrderCreatedEvent(Long orderId, Long customerId, Set<OrderItem> items) {
}