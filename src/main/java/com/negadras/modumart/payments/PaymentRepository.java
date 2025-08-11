package com.negadras.modumart.payments;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
    
    Optional<Payment> findByOrderId(Long orderId);
    
    List<Payment> findByCustomerId(Long customerId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    @Query("SELECT * FROM payments WHERE customer_id = :customerId AND status = :status")
    List<Payment> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") PaymentStatus status);
    
    @Query("SELECT * FROM payments ORDER BY created_at DESC LIMIT :limit")
    List<Payment> findRecentPayments(@Param("limit") Integer limit);
    
    Optional<Payment> findByTransactionId(String transactionId);
}