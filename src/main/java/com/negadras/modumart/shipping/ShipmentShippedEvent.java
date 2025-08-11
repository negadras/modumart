package com.negadras.modumart.shipping;

public record ShipmentShippedEvent(Long shipmentId, Long orderId, Long customerId, String trackingNumber, ShippingCarrier carrier) {
}