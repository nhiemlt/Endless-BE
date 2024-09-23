package com.datn.endless.repositories;

import com.datn.endless.entities.Notification;
import com.datn.endless.entities.Notificationrecipient;
import com.datn.endless.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}