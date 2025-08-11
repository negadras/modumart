package com.negadras.modumart.customers;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    @Query("SELECT * FROM customers WHERE first_name ILIKE %:name% OR last_name ILIKE %:name%")
    List<Customer> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT * FROM customers ORDER BY created_at DESC LIMIT :limit")
    List<Customer> findRecentCustomers(@Param("limit") Integer limit);
}