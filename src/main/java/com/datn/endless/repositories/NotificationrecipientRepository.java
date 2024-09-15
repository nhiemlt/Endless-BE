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
    @Query("SELECT r FROM Notificationrecipient r JOIN r.notificationID n WHERE r.userID = :userId ORDER BY n.notificationDate DESC")
    Page<Notificationrecipient> findByUserID(@Param("userId") User user, Pageable pageable);


    void deleteByNotificationID(Notification notification);
}