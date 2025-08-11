package com.negadras.modumart.shipping;

public record ShipmentDeliveryFailedEvent(Long shipmentId, Long orderId) {
}