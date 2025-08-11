package com.negadras.modumart.notifications;

import com.negadras.modumart.catalog.ProductOutOfStockEvent;
import com.negadras.modumart.customers.CustomerRegisteredEvent;
import com.negadras.modumart.orders.*;
import com.negadras.modumart.payments.PaymentCompletedEvent;
import com.negadras.modumart.payments.PaymentFailedEvent;
import com.negadras.modumart.shipping.ShipmentDeliveredEvent;
import com.negadras.modumart.shipping.ShipmentDeliveryFailedEvent;
import com.negadras.modumart.shipping.ShipmentShippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ModumartEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ModumartEventHandler.class);
    
    private final NotificationService notificationService;
    
    public ModumartEventHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @EventListener
    public void handleCustomerRegistered(CustomerRegisteredEvent event) {
        logger.info("Processing customer registered event for customer: {}", event.customerId());
        
        NotificationRequest request = new NotificationRequest(
                event.customerId(),
                NotificationType.WELCOME,
                NotificationChannel.EMAIL,
                event.email(),
                Map.of("customerName", "New Customer"),
                "Customer",
                event.customerId()
        );
        
        Notification notification = notificationService.createNotification(request);
        notificationService.sendNotification(notification.id());
    }
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        logger.info("Processing order created event for order: {}", event.orderId());
        
        // Calculate total from order items
        double total = event.items().stream()
                .mapToDouble(item -> item.unitPrice().doubleValue() * item.quantity())
                .sum();
        
        NotificationRequest request = new NotificationRequest(
                event.customerId(),
                NotificationType.ORDER_CONFIRMATION,
                NotificationChannel.EMAIL,
                "customer@example.com", // In real system, would fetch from customer
                Map.of(
                    "orderId", event.orderId().toString(),
                    "amount", String.format("%.2f", total)
                ),
                "Order",
                event.orderId()
        );
        
        Notification notification = notificationService.createNotification(request);
        notificationService.sendNotification(notification.id());
    }
    
    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        logger.info("Processing order cancelled event for order: {}", event.orderId());
        
        NotificationRequest request = new NotificationRequest(
                event.customerId(),
                NotificationType.ORDER_CANCELLED,
                NotificationChannel.EMAIL,
                "customer@example.com",
                Map.of("orderId", event.orderId().toString()),
                "Order",
                event.orderId()
        );
        
        Notification notification = notificationService.createNotification(request);
        notificationService.sendNotification(notification.id());
    }
    
    @EventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        logger.info("Processing payment completed event for order: {}", event.orderId());
        
        NotificationRequest request = new NotificationRequest(
                event.customerId(),
                NotificationType.PAYMENT_RECEIVED,
                NotificationChannel.EMAIL,
                "customer@example.com",
                Map.of(
                    "orderId", event.orderId().toString(),
                    "amount", event.amount().toString(),
                    "transactionId", "TXN-" + event.paymentId()
                ),
                "Payment",
                event.paymentId()
        );
        
        Notification notification = notificationService.createNotification(request);
        notificationService.sendNotification(notification.id());
    }
    
    @EventListener
    public void handlePaymentFailed(PaymentFailedEvent event) {
        logger.info("Processing payment failed event for order: {}", event.orderId());
        
        NotificationRequest request = new NotificationRequest(
                null, // Customer ID not available in event, would need to be looked up
                NotificationType.PAYMENT_FAILED,
                NotificationChannel.EMAIL,
                "customer@example.com",
                Map.of(
                    "orderId", event.orderId().toString(),
                    "reason", event.reason()
                ),
                "Payment",
                event.paymentId()
        );
        
        Notification notification = notificationService.createNotification(request);
        notificationService.sendNotification(notification.id());
    }
    
    @EventListener
    public void handleShipmentShipped(ShipmentShippedEvent event) {
        logger.info("Processing shipment shipped event for order: {}", event.orderId());
        
        NotificationRequest request = new NotificationRequest(
                event.customerId(),
                NotificationType.ORDER_SHIPPED,
                NotificationChannel.EMAIL,
                "customer@example.com",
                Map.of(
                    "orderId", event.orderId().toString(),
                    "carrier", event.carrier().toString(),
                    "trackingNumber", event.trackingNumber()
                ),
                "Shipment",
                event.shipmentId()
        );
        
        Notification notification = notificationService.createNotification(request);
        notificationService.sendNotification(notification.id());
        
        // Also send SMS notification
        NotificationRequest smsRequest = new NotificationRequest(
                event.customerId(),
                NotificationType.SHIPMENT_UPDATE,
                NotificationChannel.SMS,
                "+1234567890", // In real system, would fetch from customer
                Map.of(
                    "orderId", event.orderId().toString(),
                    "status", "Your order has shipped! Track: " + event.trackingNumber()
                ),
                "Shipment",
                event.shipmentId()
        );
        
        Notification smsNotification = notificationService.createNotification(smsRequest);
        notificationService.sendNotification(smsNotification.id());
    }
    
    @EventListener
    public void handleShipmentDelivered(ShipmentDeliveredEvent event) {
        logger.info("Processing shipment delivered event for order: {}", event.orderId());
        
        NotificationRequest request = new NotificationRequest(
                event.customerId(),
                NotificationType.ORDER_DELIVERED,
                NotificationChannel.EMAIL,
                "customer@example.com",
                Map.of("orderId", event.orderId().toString()),
                "Shipment",
                event.shipmentId()
        );
        
        Notification notification = notificationService.createNotification(request);
        notificationService.sendNotification(notification.id());
    }
    
    @EventListener
    public void handleShipmentDeliveryFailed(ShipmentDeliveryFailedEvent event) {
        logger.info("Processing shipment delivery failed event for order: {}", event.orderId());
        
        NotificationRequest request = new NotificationRequest(
                null, // Customer ID not available in event
                NotificationType.DELIVERY_ATTEMPT_FAILED,
                NotificationChannel.EMAIL,
                "customer@example.com",
                Map.of("orderId", event.orderId().toString()),
                "Shipment",
                event.shipmentId()
        );
        
        Notification notification = notificationService.createNotification(request);
        notificationService.sendNotification(notification.id());
    }
    
    @EventListener
    public void handleProductOutOfStock(ProductOutOfStockEvent event) {
        logger.info("Processing product out of stock event for product: {}", event.productId());
        
        // In a real system, would notify customers who have this product in wishlist/cart
        NotificationRequest request = new NotificationRequest(
                null, // Would need to find affected customers
                NotificationType.PRODUCT_OUT_OF_STOCK,
                NotificationChannel.EMAIL,
                "customer@example.com",
                Map.of("productName", "Product #" + event.productId()),
                "Product",
                event.productId()
        );
        
        Notification notification = notificationService.createNotification(request);
        notificationService.sendNotification(notification.id());
    }
}