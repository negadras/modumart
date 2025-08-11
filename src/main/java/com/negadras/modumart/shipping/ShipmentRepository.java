package com.negadras.modumart.shipping;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends CrudRepository<Shipment, Long> {
    
    Optional<Shipment> findByOrderId(Long orderId);
    
    List<Shipment> findByCustomerId(Long customerId);
    
    List<Shipment> findByStatus(ShipmentStatus status);
    
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    
    @Query("SELECT * FROM shipments WHERE customer_id = :customerId AND status = :status")
    List<Shipment> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") ShipmentStatus status);
    
    @Query("SELECT * FROM shipments ORDER BY created_at DESC LIMIT :limit")
    List<Shipment> findRecentShipments(@Param("limit") Integer limit);
    
    @Query("SELECT * FROM shipments WHERE status IN ('SHIPPED', 'IN_TRANSIT', 'OUT_FOR_DELIVERY')")
    List<Shipment> findActiveShipments();
}