package com.datn.endless.repositories;

import com.datn.endless.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface NotificationRepository extends JpaRepository<Notification, String> {
    // Tìm tất cả thông báo với phân trang và lọc
    @Query("SELECT n FROM Notification n WHERE " +
            "(:text IS NULL OR n.title LIKE %:text%) AND " +
            "(:text IS NULL OR n.content LIKE %:text%) AND " +
            "(:type IS NULL OR n.type LIKE %:type%)")
    Page<Notification> findAllNotifications(
            @Param("text") String text,
            @Param("type") String type,
            Pageable pageable);
}