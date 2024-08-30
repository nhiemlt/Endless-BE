package com.datn.endless.repositories;

import com.datn.endless.entities.Notificationrecipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationrecipientRepository extends JpaRepository<Notificationrecipient, String> {
}