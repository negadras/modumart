package com.negadras.modumart.shipping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShippingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ShippingService.class);
    
    private final ShipmentRepository shipmentRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public ShippingService(ShipmentRepository shipmentRepository, ApplicationEventPublisher eventPublisher) {
        this.shipmentRepository = shipmentRepository;
        this.eventPublisher = eventPublisher;
    }
    
    public List<Shipment> getAllShipments() {
        return (List<Shipment>) shipmentRepository.findAll();
    }
    
    public Optional<Shipment> getShipmentById(Long id) {
        return shipmentRepository.findById(id);
    }
    
    public Optional<Shipment> getShipmentByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId);
    }
    
    public Optional<Shipment> getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }
    
    public List<Shipment> getShipmentsByCustomer(Long customerId) {
        return shipmentRepository.findByCustomerId(customerId);
    }
    
    public Shipment createShipment(ShipmentRequest request) {
        logger.info("Creating shipment for order: {}", request.orderId());
        
        Shipment shipment = new Shipment(
                null,
                request.orderId(),
                request.customerId(),
                request.shippingAddress(),
                ShipmentStatus.PENDING,
                selectCarrier(),
                null,
                LocalDateTime.now(),
                null,
                null,
                null
        );
        
        Shipment savedShipment = shipmentRepository.save(shipment);
        eventPublisher.publishEvent(new ShipmentCreatedEvent(savedShipment.id(), savedShipment.orderId(), savedShipment.customerId()));
        
        return savedShipment;
    }
    
    public Optional<Shipment> prepareShipment(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .map(shipment -> {
                    logger.info("Preparing shipment: {}", shipmentId);
                    Shipment prepared = shipment.withStatus(ShipmentStatus.PREPARING);
                    Shipment saved = shipmentRepository.save(prepared);
                    
                    eventPublisher.publishEvent(new ShipmentPreparingEvent(saved.id(), saved.orderId()));
                    return saved;
                });
    }
    
    public Optional<Shipment> shipOrder(Long shipmentId) {
        return shipmentRepository.findById(shipmentId)
                .filter(shipment -> shipment.status() == ShipmentStatus.PREPARING)
                .map(shipment -> {
                    logger.info("Shipping order: {}", shipment.orderId());
                    
                    String trackingNumber = generateTrackingNumber(shipment.carrier());
                    Shipment shipped = shipment
                            .withTrackingNumber(trackingNumber)
                            .withShippedAt(LocalDateTime.now());
                    
                    Shipment saved = shipmentRepository.save(shipped);
                    
                    eventPublisher.publishEvent(new ShipmentShippedEvent(
                            saved.id(), 
                            saved.orderId(), 
                            saved.customerId(),
                            saved.trackingNumber(),
                            saved.carrier()
                    ));
                    
                    return saved;
                });
    }
    
    public Optional<Shipment> updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus) {
        return shipmentRepository.findById(shipmentId)
                .map(existing -> {
                    logger.info("Updating shipment {} status to {}", shipmentId, newStatus);
                    Shipment updated = existing.withStatus(newStatus);
                    Shipment saved = shipmentRepository.save(updated);
                    
                    publishStatusChangeEvent(saved, newStatus);
                    return saved;
                });
    }
    
    public Optional<Shipment> deliverShipment(Long shipmentId, String deliveryNotes) {
        return shipmentRepository.findById(shipmentId)
                .map(shipment -> {
                    logger.info("Marking shipment as delivered: {}", shipmentId);
                    Shipment delivered = shipment.withDeliveredAt(LocalDateTime.now(), deliveryNotes);
                    Shipment saved = shipmentRepository.save(delivered);
                    
                    eventPublisher.publishEvent(new ShipmentDeliveredEvent(
                            saved.id(),
                            saved.orderId(),
                            saved.customerId(),
                            saved.deliveredAt()
                    ));
                    
                    return saved;
                });
    }
    
    public List<Shipment> getActiveShipments() {
        return shipmentRepository.findActiveShipments();
    }
    
    public List<Shipment> getRecentShipments(Integer limit) {
        return shipmentRepository.findRecentShipments(limit != null ? limit : 10);
    }
    
    private ShippingCarrier selectCarrier() {
        // Simple carrier selection logic for demo
        ShippingCarrier[] carriers = ShippingCarrier.values();
        return carriers[(int) (Math.random() * carriers.length)];
    }
    
    private String generateTrackingNumber(ShippingCarrier carrier) {
        String prefix = switch (carrier) {
            case FEDEX -> "FX";
            case UPS -> "UP";
            case DHL -> "DH";
            case USPS -> "US";
            case LOCAL_DELIVERY -> "LD";
        };
        return prefix + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
    
    private void publishStatusChangeEvent(Shipment shipment, ShipmentStatus newStatus) {
        switch (newStatus) {
            case IN_TRANSIT -> eventPublisher.publishEvent(new ShipmentInTransitEvent(shipment.id(), shipment.orderId(), shipment.trackingNumber()));
            case OUT_FOR_DELIVERY -> eventPublisher.publishEvent(new ShipmentOutForDeliveryEvent(shipment.id(), shipment.orderId(), shipment.customerId()));
            case FAILED_DELIVERY -> eventPublisher.publishEvent(new ShipmentDeliveryFailedEvent(shipment.id(), shipment.orderId()));
        }
    }
}