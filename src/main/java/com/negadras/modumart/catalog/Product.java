package com.negadras.modumart.catalog;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("products")
public record Product(
        @Id Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String category
) {
    public Product withId(Long id) {
        return new Product(id, name, description, price, stock, category);
    }
    
    public Product withStock(Integer newStock) {
        return new Product(id, name, description, price, newStock, category);
    }
}