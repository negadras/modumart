package com.negadras.modumart.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;
    private final NotificationTemplateService templateService;
    private final ApplicationEventPublisher eventPublisher;
    
    public NotificationService(NotificationRepository notificationRepository, 
                             NotificationTemplateService templateService,
                             ApplicationEventPublisher eventPublisher) {
        this.notificationRepository = notificationRepository;
        this.templateService = templateService;
        this.eventPublisher = eventPublisher;
    }
    
    public List<Notification> getAllNotifications() {
        return (List<Notification>) notificationRepository.findAll();
    }
    
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }
    
    public List<Notification> getNotificationsByCustomer(Long customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }
    
    public List<Notification> getRecentNotificationsByCustomer(Long customerId, Integer limit) {
        return notificationRepository.findRecentByCustomerId(customerId, limit != null ? limit : 10);
    }
    
    public List<Notification> getUnreadNotifications(Long customerId) {
        return notificationRepository.findByCustomerIdAndStatus(customerId, NotificationStatus.SENT);
    }
    
    public Notification createNotification(NotificationRequest request) {
        logger.info("Creating notification for customer: {} of type: {}", request.customerId(), request.type());
        
        String title = templateService.generateTitle(request.type(), request.templateData());
        String message = templateService.generateMessage(request.type(), request.templateData());
        
        Notification notification = Notification.create(
                request.customerId(),
                request.type(),
                request.channel(),
                title,
                message,
                request.recipient(),
                request.relatedEntityType(),
                request.relatedEntityId()
        );
        
        Notification saved = notificationRepository.save(notification);
        
        eventPublisher.publishEvent(new NotificationCreatedEvent(saved.id(), saved.customerId(), saved.type()));
        
        return saved;
    }
    
    public Optional<Notification> sendNotification(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .filter(notification -> notification.status() == NotificationStatus.PENDING)
                .map(notification -> {
                    logger.info("Sending notification: {} via {}", notificationId, notification.channel());
                    
                    boolean sent = simulateSendNotification(notification);
                    
                    if (sent) {
                        Notification sentNotification = notification.withSentAt(LocalDateTime.now());
                        Notification saved = notificationRepository.save(sentNotification);
                        
                        eventPublisher.publishEvent(new NotificationSentEvent(saved.id(), saved.customerId(), saved.channel()));
                        logger.info("Notification sent successfully: {}", notificationId);
                        
                        return saved;
                    } else {
                        Notification failed = notification.withStatus(NotificationStatus.FAILED);
                        Notification saved = notificationRepository.save(failed);
                        
                        eventPublisher.publishEvent(new NotificationFailedEvent(saved.id(), saved.customerId(), "Failed to send"));
                        logger.warn("Failed to send notification: {}", notificationId);
                        
                        return saved;
                    }
                });
    }
    
    public Optional<Notification> markAsRead(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .filter(notification -> notification.status() == NotificationStatus.SENT)
                .map(notification -> {
                    logger.debug("Marking notification as read: {}", notificationId);
                    Notification read = notification.withStatus(NotificationStatus.READ);
                    return notificationRepository.save(read);
                });
    }
    
    public void sendPendingNotifications() {
        List<Notification> pending = notificationRepository.findByStatus(NotificationStatus.PENDING);
        logger.info("Found {} pending notifications to send", pending.size());
        
        pending.forEach(notification -> sendNotification(notification.id()));
    }
    
    public void retryFailedNotifications() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        List<Notification> oldPending = notificationRepository.findPendingNotificationsOlderThan(cutoff);
        
        logger.info("Found {} old pending notifications to retry", oldPending.size());
        
        oldPending.forEach(notification -> {
            logger.info("Retrying notification: {}", notification.id());
            sendNotification(notification.id());
        });
    }
    
    public List<Notification> getNotificationsByRelatedEntity(String entityType, Long entityId) {
        return notificationRepository.findByRelatedEntity(entityType, entityId);
    }
    
    private boolean simulateSendNotification(Notification notification) {
        // Simulate different success rates based on channel
        return switch (notification.channel()) {
            case EMAIL -> Math.random() > 0.05; // 95% success rate
            case SMS -> Math.random() > 0.1;   // 90% success rate
            case PUSH -> Math.random() > 0.15;  // 85% success rate
            case IN_APP -> Math.random() > 0.02; // 98% success rate
        };
    }
}