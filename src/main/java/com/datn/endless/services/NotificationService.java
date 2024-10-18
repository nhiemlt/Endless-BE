package com.datn.endless.services;

import com.datn.endless.dtos.NotificationRecipientDTO;
import com.datn.endless.entities.Notification;
import com.datn.endless.entities.Notificationrecipient;
import com.datn.endless.entities.User;
import com.datn.endless.exceptions.ResourceNotFoundException;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.models.NotificationModel;
import com.datn.endless.repositories.NotificationRepository;
import com.datn.endless.repositories.NotificationrecipientRepository;
import com.datn.endless.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationrecipientRepository notificationRecipientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginInfomation userLoginInfomation;
    @Autowired
    private NotificationrecipientRepository notificationrecipientRepository;

    public Map<String, Object> sendNotification(@Valid NotificationModel notificationModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildValidationErrorResponse(bindingResult);
        }

        try {
            Notification notification = createNotification(notificationModel);
            notificationRepository.save(notification);

            saveNotificationRecipients(notification, notificationModel.getUserIds());

            return buildSuccessResponse("Notification sent successfully!");
        } catch (Exception e) {
            return buildErrorResponse("Failed to send notification: " + e.getMessage());
        }
    }

    private Notification createNotification(NotificationModel notificationModel) {
        Notification notification = new Notification();
        notification.setNotificationID(UUID.randomUUID().toString());
        notification.setTitle(notificationModel.getTitle());
        notification.setContent(notificationModel.getContent());
        notification.setType(notificationModel.getType());
        notification.setNotificationDate(Instant.now());
        notification.setStatus("SENT");
        return notification;
    }

    private void saveNotificationRecipients(Notification notification, List<String> userIds) {
        userIds.forEach(userId -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid User ID: " + userId));
            Notificationrecipient recipient = new Notificationrecipient();
            recipient.setNotificationRecipientID(UUID.randomUUID().toString());
            recipient.setNotificationID(notification);
            recipient.setUserID(user);
            recipient.setStatus("UNREAD");
            notificationRecipientRepository.save(recipient);
        });
    }

    private Map<String, Object> buildValidationErrorResponse(BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Validation failed");
        response.put("errors", bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
        return response;
    }

    private Map<String, Object> buildSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> buildErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    public Map<String, Object> markAsRead(String notificationRecipientId) {
        try {
            Notificationrecipient recipient = notificationRecipientRepository.findById(notificationRecipientId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Notification Recipient ID: " + notificationRecipientId));
            if(!recipient.getUserID().getUsername().equals(userLoginInfomation.getCurrentUsername())){
                throw new UserNotFoundException("User not found!");
            }
            recipient.setStatus("READ");
            notificationRecipientRepository.save(recipient);

            return buildSuccessResponse("Notification marked as read.");
        } catch (Exception e) {
            return buildErrorResponse("Failed to mark notification as read: " + e.getMessage());
        }
    }

    public Page<NotificationRecipientDTO> getNotificationsByUserId(Pageable pageable) {
        User user = userRepository.findByUsername(userLoginInfomation.getCurrentUsername());
        if(user==null){
            throw new UserNotFoundException("User not found");
        }
        List<Notificationrecipient> allRecipients = notificationRecipientRepository.findAllByUserID(user.getUserID());
        allRecipients.sort(Comparator.comparing((Notificationrecipient nr) -> nr.getNotificationID().getNotificationDate()).reversed());

        return paginateAndConvertToDTO(allRecipients, pageable);
    }

    public Page<NotificationRecipientDTO> markAllAsRead(Pageable pageable) {
        User user = userRepository.findByUsername(userLoginInfomation.getCurrentUsername());
        if(user==null){
            throw new UserNotFoundException("User not found");
        }
        List<Notificationrecipient> allRecipients = notificationRecipientRepository.findAllByUserID(user.getUserID());
        for(Notificationrecipient recipient : allRecipients) {
            recipient.setStatus("READ");
            notificationRecipientRepository.save(recipient);
        }
        allRecipients.sort(Comparator.comparing((Notificationrecipient nr) -> nr.getNotificationID().getNotificationDate()).reversed());

        return paginateAndConvertToDTO(allRecipients, pageable);
    }

    public Page<NotificationRecipientDTO> getNotificationsByUserLogin(Pageable pageable) {
        User user = userRepository.findByUsername(userLoginInfomation.getCurrentUsername());
        if(user==null){
            throw new UserNotFoundException("User not found");
        }

        List<Notificationrecipient> allRecipients = notificationRecipientRepository.findAllByUserID(user.getUserID());
        allRecipients.sort(Comparator.comparing((Notificationrecipient nr) -> nr.getNotificationID().getNotificationDate()).reversed());

        return paginateAndConvertToDTO(allRecipients, pageable);
    }

    private Page<NotificationRecipientDTO> paginateAndConvertToDTO(List<Notificationrecipient> recipients, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recipients.size());
        List<Notificationrecipient> pagedRecipients = recipients.subList(start, end);

        List<NotificationRecipientDTO> dtoList = pagedRecipients.stream().map(recipient -> {
            NotificationRecipientDTO dto = new NotificationRecipientDTO();
            dto.setNotificationRecipientID(recipient.getNotificationRecipientID());
            dto.setNotificationID(recipient.getNotificationID().getNotificationID());
            dto.setStatus(recipient.getStatus());
            dto.setNotificationTitle(recipient.getNotificationID().getTitle());
            dto.setUserName(recipient.getUserID().getUsername());
            dto.setContent(recipient.getNotificationID().getContent());
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, recipients.size());
    }

    @Transactional
    public Map<String, Object> deleteNotification(String notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Notification ID: " + notificationId));

            notificationRecipientRepository.deleteByNotificationID(notification);
            notificationRepository.delete(notification);

            return buildSuccessResponse("Notification deleted successfully.");
        } catch (EmptyResultDataAccessException e) {
            return buildErrorResponse("Notification not found: " + notificationId);
        } catch (Exception e) {
            return buildErrorResponse("Failed to delete notification: " + e.getMessage());
        }
    }

    public Long getUnreadNotificationCount() {
        return notificationRecipientRepository.countUnreadNotifications(userLoginInfomation.getCurrentUsername());
    }

    @Transactional
    public ResponseEntity<String> deleteNotificationReception(String notificationRecipientID) {
        Notificationrecipient notificationrecipient = notificationRecipientRepository.findById(notificationRecipientID)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationRecipient not found with ID: " + notificationRecipientID));

        if (notificationrecipient.getUserID().getUsername().equals(userLoginInfomation.getCurrentUsername())) {
            notificationRecipientRepository.delete(notificationrecipient);
            return ResponseEntity.ok("Notification deleted successfully");
        } else {
            throw new UserNotFoundException("User not found or not authorized to delete this notification");
        }
    }

}
