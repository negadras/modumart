package com.negadras.modumart.shipping;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShippingController {
    
    private final ShippingService shippingService;
    
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }
    
    @GetMapping
    public List<Shipment> getAllShipments() {
        return shippingService.getAllShipments();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        return shippingService.getShipmentById(id)
                .map(shipment -> ResponseEntity.ok(shipment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Shipment> getShipmentByOrderId(@PathVariable Long orderId) {
        return shippingService.getShipmentByOrderId(orderId)
                .map(shipment -> ResponseEntity.ok(shipment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Shipment> trackShipment(@PathVariable String trackingNumber) {
        return shippingService.getShipmentByTrackingNumber(trackingNumber)
                .map(shipment -> ResponseEntity.ok(shipment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/customer/{customerId}")
    public List<Shipment> getShipmentsByCustomer(@PathVariable Long customerId) {
        return shippingService.getShipmentsByCustomer(customerId);
    }
    
    @PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody ShipmentRequest request) {
        Shipment shipment = shippingService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
    }
    
    @PutMapping("/{id}/prepare")
    public ResponseEntity<Shipment> prepareShipment(@PathVariable Long id) {
        return shippingService.prepareShipment(id)
                .map(shipment -> ResponseEntity.ok(shipment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/ship")
    public ResponseEntity<Shipment> shipOrder(@PathVariable Long id) {
        return shippingService.shipOrder(id)
                .map(shipment -> ResponseEntity.ok(shipment))
                .orElse(ResponseEntity.badRequest().build());
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Shipment> updateShipmentStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        return shippingService.updateShipmentStatus(id, request.status())
                .map(shipment -> ResponseEntity.ok(shipment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/deliver")
    public ResponseEntity<Shipment> deliverShipment(@PathVariable Long id, @RequestBody DeliveryRequest request) {
        return shippingService.deliverShipment(id, request.deliveryNotes())
                .map(shipment -> ResponseEntity.ok(shipment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/active")
    public List<Shipment> getActiveShipments() {
        return shippingService.getActiveShipments();
    }
    
    @GetMapping("/recent")
    public List<Shipment> getRecentShipments(@RequestParam(defaultValue = "10") Integer limit) {
        return shippingService.getRecentShipments(limit);
    }
    
    public record UpdateStatusRequest(ShipmentStatus status) {}
    public record DeliveryRequest(String deliveryNotes) {}
}