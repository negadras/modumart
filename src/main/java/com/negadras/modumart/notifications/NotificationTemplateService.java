package com.negadras.modumart.notifications;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationTemplateService {
    
    public String generateTitle(NotificationType type, Map<String, Object> data) {
        return switch (type) {
            case WELCOME -> "Welcome to ModuMart!";
            case ORDER_CONFIRMATION -> "Order Confirmed #" + data.getOrDefault("orderId", "N/A");
            case ORDER_SHIPPED -> "Your Order Has Shipped!";
            case ORDER_DELIVERED -> "Order Delivered Successfully";
            case PAYMENT_RECEIVED -> "Payment Received";
            case PAYMENT_FAILED -> "Payment Failed";
            case PRODUCT_OUT_OF_STOCK -> "Product Out of Stock";
            case SHIPMENT_UPDATE -> "Shipment Update";
            case ORDER_CANCELLED -> "Order Cancelled";
            case DELIVERY_ATTEMPT_FAILED -> "Delivery Attempt Failed";
            case ACCOUNT_UPDATE -> "Account Updated";
        };
    }
    
    public String generateMessage(NotificationType type, Map<String, Object> data) {
        return switch (type) {
            case WELCOME -> String.format("Welcome %s! Thank you for joining ModuMart. Start shopping for amazing products today!", 
                    data.getOrDefault("customerName", "valued customer"));
                    
            case ORDER_CONFIRMATION -> String.format("Your order #%s has been confirmed. Total amount: $%s. We'll notify you when it ships!", 
                    data.getOrDefault("orderId", "N/A"), data.getOrDefault("amount", "0.00"));
                    
            case ORDER_SHIPPED -> String.format("Great news! Your order #%s has shipped via %s. Tracking number: %s", 
                    data.getOrDefault("orderId", "N/A"), 
                    data.getOrDefault("carrier", "courier"),
                    data.getOrDefault("trackingNumber", "N/A"));
                    
            case ORDER_DELIVERED -> String.format("Your order #%s has been delivered successfully. Thank you for shopping with ModuMart!", 
                    data.getOrDefault("orderId", "N/A"));
                    
            case PAYMENT_RECEIVED -> String.format("We've received your payment of $%s for order #%s. Transaction ID: %s", 
                    data.getOrDefault("amount", "0.00"),
                    data.getOrDefault("orderId", "N/A"),
                    data.getOrDefault("transactionId", "N/A"));
                    
            case PAYMENT_FAILED -> String.format("Payment failed for order #%s. Reason: %s. Please try again or contact support.", 
                    data.getOrDefault("orderId", "N/A"),
                    data.getOrDefault("reason", "Unknown error"));
                    
            case PRODUCT_OUT_OF_STOCK -> String.format("Sorry, product \"%s\" is currently out of stock. We'll notify you when it's available again.", 
                    data.getOrDefault("productName", "requested item"));
                    
            case SHIPMENT_UPDATE -> String.format("Shipment update for order #%s: %s", 
                    data.getOrDefault("orderId", "N/A"),
                    data.getOrDefault("status", "Status updated"));
                    
            case ORDER_CANCELLED -> String.format("Your order #%s has been cancelled. If you have any questions, please contact support.", 
                    data.getOrDefault("orderId", "N/A"));
                    
            case DELIVERY_ATTEMPT_FAILED -> String.format("We attempted to deliver your order #%s but were unsuccessful. We'll try again soon.", 
                    data.getOrDefault("orderId", "N/A"));
                    
            case ACCOUNT_UPDATE -> String.format("Your account information has been updated successfully. If you didn't make these changes, please contact support immediately.");
        };
    }
}