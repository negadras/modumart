package com.negadras.modumart.shipping;

public record ShipmentOutForDeliveryEvent(Long shipmentId, Long orderId, Long customerId) {
}