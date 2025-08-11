package com.negadras.modumart.catalog;

public record ProductOutOfStockEvent(Long productId, Integer availableStock, Integer requestedQuantity) {
}
