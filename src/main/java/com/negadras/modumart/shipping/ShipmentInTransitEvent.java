package com.negadras.modumart.shipping;

public record ShipmentInTransitEvent(Long shipmentId, Long orderId, String trackingNumber) {
}