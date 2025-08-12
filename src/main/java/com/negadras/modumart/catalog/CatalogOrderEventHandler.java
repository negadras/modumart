package com.negadras.modumart.catalog;

import com.negadras.modumart.orders.OrderCancelledEvent;
import com.negadras.modumart.orders.OrderCreatedEvent;
import com.negadras.modumart.orders.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CatalogOrderEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(CatalogOrderEventHandler.class);

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CatalogOrderEventHandler(ProductRepository productRepository, ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        logger.info("Processing order created event for order: {}", event.orderId());

        for (OrderItem item : event.items()) {
            reduceProductStock(item.productId(), item.quantity());
        }

        logger.info("Stock reduced for order: {}", event.orderId());
    }

    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        logger.info("Processing order cancelled event for order: {}", event.orderId());

        // Note: In a real system, we need to get the order items from the cancelled order
        // For demo purposes, we'll emit an event indicating the order was cancelled
        eventPublisher.publishEvent(new OrderStockRestorationNeededEvent(event.orderId()));

        logger.info("Order cancellation processed for order: {}", event.orderId());
    }

    private void reduceProductStock(Long productId, Integer quantity) {
        productRepository.findById(productId).ifPresentOrElse(
            product -> {
                if (product.stock() >= quantity) {
                    Product updated = product.withStock(product.stock() - quantity);
                    productRepository.save(updated);
                    logger.debug("Reduced stock for product {} by {} units. New stock: {}",
                            productId, quantity, updated.stock());
                } else {
                    logger.warn("Insufficient stock for product {}. Available: {}, Requested: {}",
                            productId, product.stock(), quantity);
                    eventPublisher.publishEvent(new ProductOutOfStockEvent(productId, product.stock(), quantity));
                }
            },
            () -> {
                logger.error("Product not found: {}", productId);
                eventPublisher.publishEvent(new ProductNotFoundEvent(productId));
            }
        );
    }
}
