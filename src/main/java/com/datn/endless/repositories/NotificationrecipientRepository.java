package com.datn.endless.repositories;

import com.datn.endless.entities.Notificationrecipient;
import com.datn.endless.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationrecipientRepository extends JpaRepository<Notificationrecipient, String> {
    List<Notificationrecipient> findByUserID(User user);
}