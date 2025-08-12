package com.negadras.modumart.analytics;

import com.negadras.modumart.orders.*;
import com.negadras.modumart.customers.CustomerRegisteredEvent;
import com.negadras.modumart.catalog.*;
import com.negadras.modumart.payments.*;
import com.negadras.modumart.shipping.*;
import com.negadras.modumart.notifications.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class AnalyticsEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsEventHandler.class);
    
    private final AnalyticsService analyticsService;
    
    public AnalyticsEventHandler(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
    
    // === Order Events ===
    
    @EventListener
    @Async
    public void handleOrderCreated(OrderCreatedEvent event) {
        logger.debug("Processing OrderCreatedEvent for analytics: orderId={}", event.orderId());
        analyticsService.recordOrderCreated(event.orderId(), event.customerId());
    }
    
    @EventListener  
    @Async
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        logger.debug("Processing OrderConfirmedEvent for analytics: orderId={}", event.orderId());
        analyticsService.recordOrderConfirmed(event.orderId(), event.customerId());
    }
    
    @EventListener
    @Async
    public void handleOrderPaid(OrderPaidEvent event) {
        logger.debug("Processing OrderPaidEvent for analytics: orderId={}, amount={}", 
                    event.orderId(), event.amount());
        analyticsService.recordOrderPaid(event.orderId(), event.customerId(), event.amount());
    }
    
    @EventListener
    @Async
    public void handleOrderShipped(OrderShippedEvent event) {
        logger.debug("Processing OrderShippedEvent for analytics: orderId={}", event.orderId());
        analyticsService.recordOrderShipped(event.orderId(), event.customerId());
    }
    
    @EventListener
    @Async
    public void handleOrderDelivered(OrderDeliveredEvent event) {
        logger.debug("Processing OrderDeliveredEvent for analytics: orderId={}", event.orderId());
        analyticsService.recordOrderDelivered(event.orderId(), event.customerId());
    }
    
    @EventListener
    @Async
    public void handleOrderCancelled(OrderCancelledEvent event) {
        logger.debug("Processing OrderCancelledEvent for analytics: orderId={}", event.orderId());
        analyticsService.recordOrderCancelled(event.orderId(), event.customerId());
    }
    
    // === Customer Events ===
    
    @EventListener
    @Async
    public void handleCustomerRegistered(CustomerRegisteredEvent event) {
        logger.debug("Processing CustomerRegisteredEvent for analytics: customerId={}", event.customerId());
        analyticsService.recordCustomerRegistered(event.customerId(), event.email());
    }
    
    // === Product Events ===
    
    @EventListener
    @Async
    public void handleProductCreated(ProductCreatedEvent event) {
        logger.debug("Processing ProductCreatedEvent for analytics: productId={}", event.productId());
        analyticsService.recordProductCreated(event.productId(), "UNKNOWN");
    }
    
    @EventListener
    @Async
    public void handleProductStockReduced(ProductStockReducedEvent event) {
        logger.debug("Processing ProductStockReducedEvent for analytics: productId={}, quantity={}", 
                    event.productId(), event.quantity());
        analyticsService.recordProductStockReduced(event.productId(), event.quantity());
    }
    
    @EventListener
    @Async
    public void handleProductOutOfStock(ProductOutOfStockEvent event) {
        logger.debug("Processing ProductOutOfStockEvent for analytics: productId={}", event.productId());
        analyticsService.recordProductOutOfStock(event.productId());
    }
    
    // === Payment Events ===
    
    @EventListener
    @Async
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        logger.debug("Processing PaymentCompletedEvent for analytics: paymentId={}, amount={}", 
                    event.paymentId(), event.amount());
        analyticsService.recordPaymentCompleted(event.paymentId(), event.orderId(), event.amount());
    }
    
    @EventListener
    @Async
    public void handlePaymentFailed(PaymentFailedEvent event) {
        logger.debug("Processing PaymentFailedEvent for analytics: paymentId={}, reason={}", 
                    event.paymentId(), event.reason());
        analyticsService.recordPaymentFailed(event.paymentId(), event.orderId(), event.reason());
    }
    
    @EventListener
    @Async
    public void handlePaymentRefunded(PaymentRefundedEvent event) {
        logger.debug("Processing PaymentRefundedEvent for analytics: paymentId={}, amount={}", 
                    event.paymentId(), event.amount());
        analyticsService.recordPaymentRefunded(event.paymentId(), event.orderId(), event.amount());
    }
    
    // === Shipping Events ===
    
    @EventListener
    @Async
    public void handleShipmentCreated(ShipmentCreatedEvent event) {
        logger.debug("Processing ShipmentCreatedEvent for analytics: shipmentId={}", event.shipmentId());
        analyticsService.recordShipmentCreated(event.shipmentId(), event.orderId());
    }
    
    @EventListener
    @Async
    public void handleShipmentDelivered(ShipmentDeliveredEvent event) {
        logger.debug("Processing ShipmentDeliveredEvent for analytics: shipmentId={}", event.shipmentId());
        analyticsService.recordShipmentDelivered(event.shipmentId(), event.orderId());
    }
    
    @EventListener
    @Async
    public void handleShipmentDeliveryFailed(ShipmentDeliveryFailedEvent event) {
        logger.debug("Processing ShipmentDeliveryFailedEvent for analytics: shipmentId={}, reason={}", 
                    event.shipmentId(), "DELIVERY_FAILED");
        analyticsService.recordShipmentDeliveryFailed(event.shipmentId(), event.orderId(), "DELIVERY_FAILED");
    }
    
    // === Notification Events ===
    
    @EventListener
    @Async
    public void handleNotificationSent(NotificationSentEvent event) {
        logger.debug("Processing NotificationSentEvent for analytics: notificationId={}, type={}", 
                    event.notificationId(), event.channel().toString());
        analyticsService.recordNotificationSent(event.notificationId(), event.customerId(), event.channel().toString());
    }
    
    @EventListener
    @Async
    public void handleNotificationFailed(NotificationFailedEvent event) {
        logger.debug("Processing NotificationFailedEvent for analytics: notificationId={}, reason={}", 
                    event.notificationId(), event.reason());
        analyticsService.recordNotificationFailed(event.notificationId(), event.customerId(), event.reason());
    }
}