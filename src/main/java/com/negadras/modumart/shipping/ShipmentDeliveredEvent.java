package com.negadras.modumart.shipping;

import java.time.LocalDateTime;

public record ShipmentDeliveredEvent(Long shipmentId, Long orderId, Long customerId, LocalDateTime deliveredAt) {
}