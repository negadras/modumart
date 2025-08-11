package com.negadras.modumart.customers;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("customers")
public record Customer(
        @Id Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        LocalDateTime createdAt
) {
    public Customer withId(Long id) {
        return new Customer(id, firstName, lastName, email, phone, address, createdAt);
    }
    
    public String fullName() {
        return firstName + " " + lastName;
    }
}