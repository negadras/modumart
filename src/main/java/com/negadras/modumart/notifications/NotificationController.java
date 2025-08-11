package com.negadras.modumart.notifications;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
                .map(notification -> ResponseEntity.ok(notification))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/customer/{customerId}")
    public List<Notification> getNotificationsByCustomer(@PathVariable Long customerId) {
        return notificationService.getNotificationsByCustomer(customerId);
    }
    
    @GetMapping("/customer/{customerId}/recent")
    public List<Notification> getRecentNotificationsByCustomer(@PathVariable Long customerId, 
                                                              @RequestParam(defaultValue = "10") Integer limit) {
        return notificationService.getRecentNotificationsByCustomer(customerId, limit);
    }
    
    @GetMapping("/customer/{customerId}/unread")
    public List<Notification> getUnreadNotifications(@PathVariable Long customerId) {
        return notificationService.getUnreadNotifications(customerId);
    }
    
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationRequest request) {
        Notification notification = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }
    
    @PutMapping("/{id}/send")
    public ResponseEntity<Notification> sendNotification(@PathVariable Long id) {
        return notificationService.sendNotification(id)
                .map(notification -> ResponseEntity.ok(notification))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id)
                .map(notification -> ResponseEntity.ok(notification))
                .orElse(ResponseEntity.badRequest().build());
    }
    
    @PostMapping("/send-pending")
    public ResponseEntity<Void> sendPendingNotifications() {
        notificationService.sendPendingNotifications();
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/retry-failed")
    public ResponseEntity<Void> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/entity/{entityType}/{entityId}")
    public List<Notification> getNotificationsByRelatedEntity(@PathVariable String entityType, 
                                                             @PathVariable Long entityId) {
        return notificationService.getNotificationsByRelatedEntity(entityType, entityId);
    }
}