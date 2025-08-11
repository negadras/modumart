package com.negadras.modumart.customers;

public record CustomerRegisteredEvent(Long customerId, String email) {
}