package com.datn.endless.services;

import com.datn.endless.dtos.NotificationDTO;
import com.datn.endless.dtos.NotificationRecipientDTO;
import com.datn.endless.entities.Notification;
import com.datn.endless.entities.Notificationrecipient;
import com.datn.endless.entities.User;
import com.datn.endless.models.NotificationModel;
import com.datn.endless.repositories.NotificationRepository;
import com.datn.endless.repositories.NotificationrecipientRepository;
import com.datn.endless.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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

    public Map<String, Object> sendNotification(@Valid NotificationModel notificationModel, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", "Validation failed");
            response.put("errors", bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
            return response;
        }

        try {
            // Create and save the notification
            Notification notification = new Notification();
            notification.setNotificationID(UUID.randomUUID().toString());
            notification.setTitle(notificationModel.getTitle());
            notification.setContent(notificationModel.getContent());
            notification.setType(notificationModel.getType());
            notification.setNotificationDate(Instant.now());
            notification.setStatus("SENT");

            notificationRepository.save(notification);

            // Assign notification to users
            for (String userId : notificationModel.getUserIds()) {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid User ID: " + userId));
                Notificationrecipient recipient = new Notificationrecipient();
                recipient.setNotificationRecipientID(UUID.randomUUID().toString());
                recipient.setNotificationID(notification);
                recipient.setUserID(user);
                recipient.setStatus("UNREAD");

                notificationRecipientRepository.save(recipient);
            }
            response.put("success", true);
            response.put("message", "Notification sent successfully!");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid input: " + e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send notification: " + e.getMessage());
        }
        return response;
    }

    public Map<String, Object> markAsRead(String notificationRecipientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notificationrecipient recipient = notificationRecipientRepository.findById(notificationRecipientId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Notification Recipient ID: " + notificationRecipientId));
            recipient.setStatus("READ");
            notificationRecipientRepository.save(recipient);

            response.put("success", true);
            response.put("message", "Notification marked as read.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid input: " + e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to mark notification as read: " + e.getMessage());
        }
        return response;
    }

    public Page<NotificationRecipientDTO> getNotificationsByUserId(String userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID: " + userId));
        Page<Notificationrecipient> recipientsPage = notificationRecipientRepository.findByUserID(user, pageable);

        return recipientsPage.map(recipient -> {
            NotificationRecipientDTO dto = new NotificationRecipientDTO();
            dto.setNotificationRecipientID(recipient.getNotificationRecipientID());
            dto.setStatus(recipient.getStatus());
            dto.setNotificationTitle(recipient.getNotificationID().getTitle());
            dto.setUserName(recipient.getUserID().getUsername());
            return dto;
        });
    }

    @Transactional
    public Map<String, Object> deleteNotification(String notificationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Notification ID: " + notificationId));

            notificationRecipientRepository.deleteByNotificationID(notification);
            notificationRepository.delete(notification);

            response.put("success", true);
            response.put("message", "Notification deleted successfully.");
        } catch (EmptyResultDataAccessException e) {
            response.put("success", false);
            response.put("message", "Notification not found: " + notificationId);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid input: " + e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete notification: " + e.getMessage());
        }
        return response;
    }
}
