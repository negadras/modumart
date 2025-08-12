package com.negadras.modumart.shipping;

import com.negadras.modumart.payments.PaymentCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ShippingPaymentEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ShippingPaymentEventHandler.class);
    
    private final ShippingService shippingService;
    
    public ShippingPaymentEventHandler(ShippingService shippingService) {
        this.shippingService = shippingService;
    }
    
    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        logger.info("Processing payment completed event for order: {}", event.orderId());
        
        // Create shipment when payment is completed
        ShipmentRequest shipmentRequest = new ShipmentRequest(
                event.orderId(),
                event.customerId(),
                "Default shipping address" // In real system, would fetch from order
        );
        
        try {
            Shipment shipment = shippingService.createShipment(shipmentRequest);
            logger.info("Shipment created for order {} with ID: {}", event.orderId(), shipment.id());
            
            // Automatically prepare the shipment
            shippingService.prepareShipment(shipment.id());
            logger.info("Shipment {} prepared for order {}", shipment.id(), event.orderId());
            
        } catch (Exception e) {
            logger.error("Failed to create shipment for order {}: {}", event.orderId(), e.getMessage());
        }
    }
}