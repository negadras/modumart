package com.negadras.modumart.payments;

import com.negadras.modumart.orders.OrderConfirmedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentsOrderEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentsOrderEventHandler.class);
    
    private final PaymentService paymentService;
    
    public PaymentsOrderEventHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @EventListener
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        logger.info("Processing order confirmed event for order: {}", event.orderId());
        
        // In a real system, we would fetch order details to get the amount
        // For demo purposes, we'll create a payment record that can be processed later
        logger.info("Order {} is confirmed and ready for payment processing", event.orderId());
        
        // The payment initiation would typically be triggered by the customer
        // through the UI, but we log that the order is ready for payment
        logger.info("Payment can now be initiated for order: {}", event.orderId());
    }
}