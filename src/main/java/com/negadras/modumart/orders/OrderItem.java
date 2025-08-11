package com.negadras.modumart.orders;

import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("order_items")
public record OrderItem(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
    public static OrderItem create(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return new OrderItem(productId, productName, quantity, unitPrice, totalPrice);
    }
}