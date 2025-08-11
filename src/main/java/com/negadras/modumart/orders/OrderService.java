package com.negadras.modumart.orders;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public OrderService(OrderRepository orderRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }
    
    public List<Order> getAllOrders() {
        return (List<Order>) orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public Order createOrder(CreateOrderRequest request) {
        BigDecimal totalAmount = request.items().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Order newOrder = new Order(
                null,
                request.customerId(),
                OrderStatus.PENDING,
                totalAmount,
                LocalDateTime.now(),
                LocalDateTime.now(),
                request.shippingAddress(),
                request.items()
        );
        
        Order savedOrder = orderRepository.save(newOrder);
        eventPublisher.publishEvent(new OrderCreatedEvent(savedOrder.id(), savedOrder.customerId(), savedOrder.items()));
        return savedOrder;
    }
    
    public Optional<Order> updateOrderStatus(Long id, OrderStatus newStatus) {
        return orderRepository.findById(id)
                .map(existing -> {
                    Order updated = existing.withStatus(newStatus);
                    Order saved = orderRepository.save(updated);
                    publishStatusChangeEvent(saved, existing.status(), newStatus);
                    return saved;
                });
    }
    
    public void cancelOrder(Long id) {
        orderRepository.findById(id).ifPresent(order -> {
            if (order.status() == OrderStatus.PENDING || order.status() == OrderStatus.CONFIRMED) {
                Order cancelled = order.withStatus(OrderStatus.CANCELLED);
                orderRepository.save(cancelled);
                eventPublisher.publishEvent(new OrderCancelledEvent(id, order.customerId()));
            }
        });
    }
    
    public List<Order> getRecentOrders(Integer limit) {
        return orderRepository.findRecentOrders(limit != null ? limit : 10);
    }
    
    private void publishStatusChangeEvent(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        switch (newStatus) {
            case CONFIRMED -> eventPublisher.publishEvent(new OrderConfirmedEvent(order.id(), order.customerId()));
            case PAID -> eventPublisher.publishEvent(new OrderPaidEvent(order.id(), order.customerId(), order.totalAmount()));
            case SHIPPED -> eventPublisher.publishEvent(new OrderShippedEvent(order.id(), order.customerId(), order.shippingAddress()));
            case DELIVERED -> eventPublisher.publishEvent(new OrderDeliveredEvent(order.id(), order.customerId()));
        }
    }
}