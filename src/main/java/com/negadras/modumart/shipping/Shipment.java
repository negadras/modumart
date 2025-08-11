package com.negadras.modumart.shipping;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("shipments")
public record Shipment(
        @Id Long id,
        Long orderId,
        Long customerId,
        String shippingAddress,
        ShipmentStatus status,
        ShippingCarrier carrier,
        String trackingNumber,
        LocalDateTime createdAt,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        String deliveryNotes
) {
    public Shipment withId(Long id) {
        return new Shipment(id, orderId, customerId, shippingAddress, status, carrier, trackingNumber, createdAt, shippedAt, deliveredAt, deliveryNotes);
    }
    
    public Shipment withStatus(ShipmentStatus status) {
        return new Shipment(id, orderId, customerId, shippingAddress, status, carrier, trackingNumber, createdAt, shippedAt, deliveredAt, deliveryNotes);
    }
    
    public Shipment withTrackingNumber(String trackingNumber) {
        return new Shipment(id, orderId, customerId, shippingAddress, status, carrier, trackingNumber, createdAt, shippedAt, deliveredAt, deliveryNotes);
    }
    
    public Shipment withShippedAt(LocalDateTime shippedAt) {
        return new Shipment(id, orderId, customerId, shippingAddress, ShipmentStatus.SHIPPED, carrier, trackingNumber, createdAt, shippedAt, deliveredAt, deliveryNotes);
    }
    
    public Shipment withDeliveredAt(LocalDateTime deliveredAt, String deliveryNotes) {
        return new Shipment(id, orderId, customerId, shippingAddress, ShipmentStatus.DELIVERED, carrier, trackingNumber, createdAt, shippedAt, deliveredAt, deliveryNotes);
    }
}