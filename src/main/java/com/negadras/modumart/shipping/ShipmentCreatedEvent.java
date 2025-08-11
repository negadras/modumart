package com.negadras.modumart.shipping;

public record ShipmentCreatedEvent(Long shipmentId, Long orderId, Long customerId) {
}