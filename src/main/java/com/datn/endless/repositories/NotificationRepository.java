package com.datn.endless.repositories;

import com.datn.endless.entities.Notification;
import com.datn.endless.entities.Notificationrecipient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    // Tìm tất cả thông báo với phân trang và lọc
    @Query("SELECT n FROM Notification n WHERE (:title IS NULL OR n.title LIKE %:title%) " +
            "AND (:status IS NULL OR n.status = :status)")
    Page<Notification> findAllNotifications(
            @Param("title") String title,
            @Param("status") String status,
            Pageable pageable);
}