package com.datn.endless.services;

import com.datn.endless.dtos.NotificationDTO;
import com.datn.endless.dtos.NotificationRecipientDTO;
import com.datn.endless.entities.Notification;
import com.datn.endless.entities.Notificationrecipient;
import com.datn.endless.entities.User;
import com.datn.endless.repositories.NotificationRepository;
import com.datn.endless.repositories.NotificationrecipientRepository;
import com.datn.endless.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationrecipientRepository notificationRecipientRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendNotification(NotificationDTO notificationDTO) {
        Notification notification = new Notification();
        notification.setNotificationID(UUID.randomUUID().toString());
        notification.setTitle(notificationDTO.getTitle());
        notification.setContent(notificationDTO.getContent());
        notification.setType(notificationDTO.getType());
        notification.setNotificationDate(Instant.now());
        notification.setStatus("SENT");

        notificationRepository.save(notification);

        for (String userId : notificationDTO.getUserIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid User ID: " + userId));
            Notificationrecipient recipient = new Notificationrecipient();
            recipient.setNotificationRecipientID(UUID.randomUUID().toString());
            recipient.setNotificationID(notification);
            recipient.setUserID(user);
            recipient.setStatus("UNREAD");

            notificationRecipientRepository.save(recipient);
        }
    }

    public List<NotificationRecipientDTO> getNotificationsByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID: " + userId));
        List<Notificationrecipient> recipients = notificationRecipientRepository.findByUserID(user);

        return recipients.stream().map(recipient -> {
            NotificationRecipientDTO dto = new NotificationRecipientDTO();
            dto.setNotificationRecipientID(recipient.getNotificationRecipientID());
            dto.setStatus(recipient.getStatus());
            dto.setNotificationTitle(recipient.getNotificationID().getTitle());
            dto.setUserName(recipient.getUserID().getUsername());
            return dto;
        }).collect(Collectors.toList());
    }

    public void markAsRead(String notificationRecipientId) {
        Notificationrecipient recipient = notificationRecipientRepository.findById(notificationRecipientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Notification Recipient ID: " + notificationRecipientId));
        recipient.setStatus("READ");
        notificationRecipientRepository.save(recipient);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void deleteNotification(String notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Notification ID: " + notificationId));
            notificationRepository.delete(notification);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Notification not found: " + notificationId, e);
        }
    }
}
