package com.negadras.modumart.catalog;

public record ProductStockReducedEvent(Long productId, Integer quantity) {
}