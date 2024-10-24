package com.datn.endless.repositories;

import com.datn.endless.entities.Notification;
import com.datn.endless.entities.Notificationrecipient;
import com.datn.endless.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationrecipientRepository extends JpaRepository<Notificationrecipient, String> {
    @Query(value = "SELECT nr.* " +
            "FROM NotificationRecipients nr " +
            "JOIN Notifications n ON nr.NotificationID = n.NotificationID " +
            "WHERE nr.UserID = :userId " +
            "ORDER BY n.NotificationDate DESC",
            nativeQuery = true)
    List<Notificationrecipient> findAllByUserID(@Param("userId") String userId);

    void deleteByNotificationID(Notification notification);

    @Query("SELECT COUNT(nr) FROM Notificationrecipient nr WHERE nr.userID.username = :userId AND nr.status = 'UNREAD'")
    Long countUnreadNotifications(@Param("userId") String username);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notificationrecipient nr WHERE nr.notificationRecipientID = :notificationRecipientID")
    void deleteNotificationReceptionByRecipientID(@Param("notificationRecipientID") String notificationRecipientID);

    @Query("SELECT nr FROM Notificationrecipient nr " +
            "JOIN nr.notificationID n " +
            "WHERE (:userId IS NULL OR nr.userID.username = :userId) " +
            "AND (:status IS NULL OR nr.status = :status) " +
            "AND (:title IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "ORDER BY n.notificationDate DESC")
    Page<Notificationrecipient> findAllNotifications(
            @Param("userId") String userId,
            @Param("status") String status,
            @Param("title") String title,
            Pageable pageable);
}