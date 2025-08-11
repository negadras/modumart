package com.negadras.modumart.orders;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Table("orders")
public record Order(
        @Id Long id,
        Long customerId,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String shippingAddress,
        @MappedCollection(idColumn = "order_id") Set<OrderItem> items
) {
    public Order withId(Long id) {
        return new Order(id, customerId, status, totalAmount, createdAt, updatedAt, shippingAddress, items);
    }
    
    public Order withStatus(OrderStatus status) {
        return new Order(id, customerId, status, totalAmount, createdAt, LocalDateTime.now(), shippingAddress, items);
    }
    
    public Order withTotalAmount(BigDecimal totalAmount) {
        return new Order(id, customerId, status, totalAmount, createdAt, LocalDateTime.now(), shippingAddress, items);
    }
}