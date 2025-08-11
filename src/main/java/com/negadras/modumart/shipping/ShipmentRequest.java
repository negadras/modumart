package com.negadras.modumart.shipping;

public record ShipmentRequest(
        Long orderId,
        Long customerId,
        String shippingAddress
) {
}