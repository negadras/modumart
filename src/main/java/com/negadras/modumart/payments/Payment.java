package com.negadras.modumart.payments;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("payments")
public record Payment(
        @Id Long id,
        Long orderId,
        Long customerId,
        BigDecimal amount,
        PaymentStatus status,
        PaymentMethod paymentMethod,
        String transactionId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String failureReason
) {
    public Payment withId(Long id) {
        return new Payment(id, orderId, customerId, amount, status, paymentMethod, transactionId, createdAt, updatedAt, failureReason);
    }
    
    public Payment withStatus(PaymentStatus status) {
        return new Payment(id, orderId, customerId, amount, status, paymentMethod, transactionId, createdAt, LocalDateTime.now(), failureReason);
    }
    
    public Payment withTransactionId(String transactionId) {
        return new Payment(id, orderId, customerId, amount, status, paymentMethod, transactionId, createdAt, LocalDateTime.now(), failureReason);
    }
    
    public Payment withFailureReason(String failureReason) {
        return new Payment(id, orderId, customerId, amount, PaymentStatus.FAILED, paymentMethod, transactionId, createdAt, LocalDateTime.now(), failureReason);
    }
}