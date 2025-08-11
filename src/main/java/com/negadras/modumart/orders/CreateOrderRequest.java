package com.negadras.modumart.orders;

import java.util.Set;

public record CreateOrderRequest(
        Long customerId,
        String shippingAddress,
        Set<OrderItem> items
) {
}