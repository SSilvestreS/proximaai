package com.proximaai.repository;

import com.proximaai.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientId(Long recipientId);
    
    List<Notification> findByRecipientIdAndStatus(Long recipientId, Notification.NotificationStatus status);
    
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, int page, int size);
    
    List<Notification> findByRecipientIdAndType(Long recipientId, Notification.NotificationType type);
    
    List<Notification> findByRecipientIdAndPriority(Long recipientId, Notification.NotificationPriority priority);
    
    List<Notification> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    List<Notification> findBySenderId(Long senderId);
    
    List<Notification> findByStatus(Notification.NotificationStatus status);
    
    List<Notification> findByType(Notification.NotificationType type);
    
    List<Notification> findByPriority(Notification.NotificationPriority priority);
    
    List<Notification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Notification> findByScheduledForBefore(LocalDateTime date);
    
    List<Notification> findByExpiresAtBefore(LocalDateTime date);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.status = 'UNREAD' ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsByRecipient(@Param("recipientId") Long recipientId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :recipientId AND n.status = 'UNREAD'")
    long countUnreadNotificationsByRecipient(@Param("recipientId") Long recipientId);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.priority = :priority ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByRecipientAndPriority(@Param("recipientId") Long recipientId, 
                                                             @Param("priority") Notification.NotificationPriority priority);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.type = :type ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByRecipientAndType(@Param("recipientId") Long recipientId, 
                                                          @Param("type") Notification.NotificationType type);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsByRecipient(@Param("recipientId") Long recipientId, 
                                                         @Param("since") LocalDateTime since);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.entityType = :entityType ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByRecipientAndEntityType(@Param("recipientId") Long recipientId, 
                                                                @Param("entityType") String entityType);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.entityId = :entityId ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByRecipientAndEntityId(@Param("recipientId") Long recipientId, 
                                                              @Param("entityId") Long entityId);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.isInternal = true ORDER BY n.createdAt DESC")
    List<Notification> findInternalNotificationsByRecipient(@Param("recipientId") Long recipientId);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.isInternal = false ORDER BY n.createdAt DESC")
    List<Notification> findExternalNotificationsByRecipient(@Param("recipientId") Long recipientId);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.readAt IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsByRecipientOrderByCreatedAt(@Param("recipientId") Long recipientId);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.readAt IS NOT NULL ORDER BY n.readAt DESC")
    List<Notification> findReadNotificationsByRecipientOrderByReadAt(@Param("recipientId") Long recipientId);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.scheduledFor <= :now ORDER BY n.scheduledFor ASC")
    List<Notification> findScheduledNotificationsReadyToSend(@Param("recipientId") Long recipientId, 
                                                            @Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.expiresAt <= :now")
    List<Notification> findExpiredNotifications(@Param("recipientId") Long recipientId, 
                                               @Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.priority IN :priorities ORDER BY n.priority DESC, n.createdAt DESC")
    List<Notification> findNotificationsByRecipientAndPriorities(@Param("recipientId") Long recipientId, 
                                                                @Param("priorities") List<Notification.NotificationPriority> priorities);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :recipientId AND n.type IN :types ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByRecipientAndTypes(@Param("recipientId") Long recipientId, 
                                                           @Param("types") List<Notification.NotificationType> types);
}
