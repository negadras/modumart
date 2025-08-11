/**
 * Orders module for managing order lifecycle in the e-commerce platform.
 * 
 * This module handles:
 * - Order creation and management
 * - Order status tracking (PENDING, CONFIRMED, PAID, SHIPPED, DELIVERED, CANCELLED)
 * - Order items and pricing calculations
 * - Customer order history
 * 
 * Events published:
 * - OrderCreatedEvent: When a new order is created
 * - OrderConfirmedEvent: When an order is confirmed
 * - OrderPaidEvent: When payment is completed
 * - OrderShippedEvent: When order is shipped
 * - OrderDeliveredEvent: When order is delivered
 * - OrderCancelledEvent: When order is cancelled
 */
package com.negadras.modumart.orders;