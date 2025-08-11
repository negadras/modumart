/**
 * Catalog module for managing products in the e-commerce platform.
 * 
 * This module handles:
 * - Product CRUD operations
 * - Stock management
 * - Category-based product filtering
 * - Order-driven stock adjustments
 * 
 * Events published:
 * - ProductCreatedEvent: When a new product is created
 * - ProductStockReducedEvent: When product stock is reduced
 * - ProductOutOfStockEvent: When insufficient stock for order
 * - ProductNotFoundEvent: When product referenced in order doesn't exist
 * - OrderStockRestorationNeededEvent: When cancelled order needs stock restoration
 * 
 * Events consumed:
 * - OrderCreatedEvent: Reduces stock for ordered products
 * - OrderCancelledEvent: Triggers stock restoration process
 */
package com.negadras.modumart.catalog;