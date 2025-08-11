/**
 * Shipping module for handling order fulfillment and delivery tracking in the e-commerce platform.
 * 
 * This module handles:
 * - Shipment creation and management
 * - Shipping status tracking (PENDING, PREPARING, SHIPPED, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, etc.)
 * - Multiple shipping carriers (FedEx, UPS, DHL, USPS, Local Delivery)
 * - Tracking number generation and management
 * - Delivery confirmation and notes
 * 
 * Events published:
 * - ShipmentCreatedEvent: When a shipment is created
 * - ShipmentPreparingEvent: When shipment preparation begins
 * - ShipmentShippedEvent: When shipment is shipped with tracking
 * - ShipmentInTransitEvent: When shipment is in transit
 * - ShipmentOutForDeliveryEvent: When shipment is out for delivery
 * - ShipmentDeliveredEvent: When shipment is successfully delivered
 * - ShipmentDeliveryFailedEvent: When delivery attempt fails
 * 
 * Events consumed:
 * - PaymentCompletedEvent: Automatically creates and prepares shipment when payment completes
 */
package com.negadras.modumart.shipping;