/**
 * Payments module for handling payment processing in the e-commerce platform.
 * 
 * This module handles:
 * - Payment initiation and processing
 * - Multiple payment methods (credit card, PayPal, etc.)
 * - Payment status tracking (PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED)
 * - Transaction ID generation and management
 * - Payment refunds
 * 
 * Events published:
 * - PaymentInitiatedEvent: When a payment is initiated
 * - PaymentCompletedEvent: When payment processing succeeds
 * - PaymentFailedEvent: When payment processing fails
 * - PaymentRefundedEvent: When a payment is refunded
 * 
 * Events consumed:
 * - OrderConfirmedEvent: Indicates order is ready for payment
 */
package com.negadras.modumart.payments;