package com.negadras.modumart.notifications;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    
    List<Notification> findByCustomerId(Long customerId);
    
    List<Notification> findByCustomerIdAndStatus(Long customerId, NotificationStatus status);
    
    List<Notification> findByType(NotificationType type);
    
    List<Notification> findByStatus(NotificationStatus status);
    
    @Query("SELECT * FROM notifications WHERE customer_id = :customerId ORDER BY created_at DESC LIMIT :limit")
    List<Notification> findRecentByCustomerId(@Param("customerId") Long customerId, @Param("limit") Integer limit);
    
    @Query("SELECT * FROM notifications WHERE status = 'PENDING' AND created_at < :cutoff")
    List<Notification> findPendingNotificationsOlderThan(@Param("cutoff") LocalDateTime cutoff);
    
    @Query("SELECT * FROM notifications WHERE related_entity_type = :entityType AND related_entity_id = :entityId")
    List<Notification> findByRelatedEntity(@Param("entityType") String entityType, @Param("entityId") Long entityId);
    
    @Query("SELECT * FROM notifications WHERE customer_id = :customerId AND channel = :channel")
    List<Notification> findByCustomerIdAndChannel(@Param("customerId") Long customerId, @Param("channel") NotificationChannel channel);
    
    @Query("SELECT COUNT(*) FROM notifications WHERE customer_id = :customerId AND status = 'SENT' AND created_at >= :since")
    Long countSentNotificationsSince(@Param("customerId") Long customerId, @Param("since") LocalDateTime since);
}