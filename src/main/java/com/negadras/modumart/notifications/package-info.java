/**
 * Notifications module for handling cross-cutting communication concerns in the e-commerce platform.
 * 
 * This module serves as a centralized notification hub that:
 * - Listens to events from all other modules
 * - Generates and sends notifications via multiple channels (Email, SMS, Push, In-App)
 * - Manages notification templates and formatting
 * - Tracks notification status and delivery
 * - Provides retry mechanisms for failed notifications
 * 
 * Notification Types:
 * - Customer lifecycle: WELCOME, ACCOUNT_UPDATE
 * - Order lifecycle: ORDER_CONFIRMATION, ORDER_SHIPPED, ORDER_DELIVERED, ORDER_CANCELLED
 * - Payment events: PAYMENT_RECEIVED, PAYMENT_FAILED
 * - Shipping events: SHIPMENT_UPDATE, DELIVERY_ATTEMPT_FAILED
 * - Inventory events: PRODUCT_OUT_OF_STOCK
 * 
 * Events published:
 * - NotificationCreatedEvent: When a notification is created
 * - NotificationSentEvent: When a notification is successfully sent
 * - NotificationFailedEvent: When notification delivery fails
 * 
 * Events consumed (from all modules):
 * - CustomerRegisteredEvent: Sends welcome notifications
 * - OrderCreatedEvent: Sends order confirmation
 * - OrderCancelledEvent: Sends cancellation notice
 * - PaymentCompletedEvent: Sends payment confirmation
 * - PaymentFailedEvent: Sends payment failure notice
 * - ShipmentShippedEvent: Sends shipping confirmation with tracking
 * - ShipmentDeliveredEvent: Sends delivery confirmation
 * - ShipmentDeliveryFailedEvent: Sends delivery failure notice
 * - ProductOutOfStockEvent: Notifies affected customers
 * 
 * This demonstrates Spring Modulith's capability for cross-cutting concerns and centralized event handling.
 */
package com.negadras.modumart.notifications;