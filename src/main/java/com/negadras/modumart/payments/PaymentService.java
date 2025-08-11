package com.negadras.modumart.payments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public PaymentService(PaymentRepository paymentRepository, ApplicationEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }
    
    public List<Payment> getAllPayments() {
        return (List<Payment>) paymentRepository.findAll();
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    public Optional<Payment> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    public List<Payment> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }
    
    public Payment initiatePayment(PaymentRequest request) {
        logger.info("Initiating payment for order: {}", request.orderId());
        
        Payment payment = new Payment(
                null,
                request.orderId(),
                request.customerId(),
                request.amount(),
                PaymentStatus.PENDING,
                request.paymentMethod(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
        
        Payment savedPayment = paymentRepository.save(payment);
        eventPublisher.publishEvent(new PaymentInitiatedEvent(savedPayment.id(), savedPayment.orderId(), savedPayment.amount()));
        
        return savedPayment;
    }
    
    public Optional<Payment> processPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(payment -> {
                    logger.info("Processing payment: {}", paymentId);
                    Payment processing = payment.withStatus(PaymentStatus.PROCESSING);
                    Payment saved = paymentRepository.save(processing);
                    
                    // Simulate payment processing
                    boolean success = simulatePaymentProcessing();
                    
                    if (success) {
                        String transactionId = generateTransactionId();
                        Payment completed = saved.withStatus(PaymentStatus.COMPLETED).withTransactionId(transactionId);
                        Payment finalPayment = paymentRepository.save(completed);
                        
                        eventPublisher.publishEvent(new PaymentCompletedEvent(finalPayment.id(), finalPayment.orderId(), finalPayment.customerId(), finalPayment.amount()));
                        logger.info("Payment completed successfully: {} with transaction ID: {}", paymentId, transactionId);
                        
                        return finalPayment;
                    } else {
                        String failureReason = "Payment processing failed - insufficient funds";
                        Payment failed = saved.withFailureReason(failureReason);
                        Payment finalPayment = paymentRepository.save(failed);
                        
                        eventPublisher.publishEvent(new PaymentFailedEvent(finalPayment.id(), finalPayment.orderId(), failureReason));
                        logger.warn("Payment failed: {} - {}", paymentId, failureReason);
                        
                        return finalPayment;
                    }
                });
    }
    
    public Optional<Payment> refundPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .filter(payment -> payment.status() == PaymentStatus.COMPLETED)
                .map(payment -> {
                    logger.info("Processing refund for payment: {}", paymentId);
                    Payment refunded = payment.withStatus(PaymentStatus.REFUNDED);
                    Payment saved = paymentRepository.save(refunded);
                    
                    eventPublisher.publishEvent(new PaymentRefundedEvent(saved.id(), saved.orderId(), saved.customerId(), saved.amount()));
                    return saved;
                });
    }
    
    public List<Payment> getRecentPayments(Integer limit) {
        return paymentRepository.findRecentPayments(limit != null ? limit : 10);
    }
    
    private boolean simulatePaymentProcessing() {
        // Simulate 90% success rate for demo
        return Math.random() > 0.1;
    }
    
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}