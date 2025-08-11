package com.negadras.modumart.orders;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT * FROM orders WHERE customer_id = :customerId AND status = :status")
    List<Order> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") OrderStatus status);
    
    @Query("SELECT * FROM orders ORDER BY created_at DESC LIMIT :limit")
    List<Order> findRecentOrders(@Param("limit") Integer limit);
}