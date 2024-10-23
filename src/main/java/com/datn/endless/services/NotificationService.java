package com.datn.endless.services;

import com.datn.endless.dtos.NotificationRecipientDTO;
import com.datn.endless.entities.Notification;
import com.datn.endless.entities.Notificationrecipient;
import com.datn.endless.entities.User;
import com.datn.endless.exceptions.ResourceNotFoundException;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.models.NotificationModel;
import com.datn.endless.models.NotificationModelForUser;
import com.datn.endless.repositories.NotificationRepository;
import com.datn.endless.repositories.NotificationrecipientRepository;
import com.datn.endless.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

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

    public Map<String, Object> sendNotification(@Valid NotificationModel notificationModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildErrorResponse("Validation failed");
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

    public void sendNotificationForOrder(@Valid NotificationModelForUser notificationModel) {
        try {
            Notification notification = createNotificationForUser(notificationModel);
            Notification notification1 = notificationRepository.save(notification);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to send notification: " + e.getMessage());
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
        notification.setNotificationrecipients(new HashSet<>()); // Khởi tạo Set nếu cần
        return notification;
    }


    private Notification createNotificationForUser(NotificationModelForUser notificationModel) {
        // Tạo một đối tượng Notification mới
        Notification notification = new Notification();
        notification.setNotificationID(UUID.randomUUID().toString());
        notification.setTitle(notificationModel.getTitle());
        notification.setContent(notificationModel.getContent());
        notification.setType(notificationModel.getType());
        notification.setNotificationDate(Instant.now());
        notification.setStatus("SENT");

        // Tìm user theo ID
        User user = userRepository.findById(notificationModel.getUserID())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Tạo Notificationrecipient mới
        Notificationrecipient recipient = createNotificationRecipient(notification, user, "UNREAD");
        notification.getNotificationrecipients().add(recipient); // Thêm recipient vào notification
        return notification;
    }

    // Phương thức tạo Notificationrecipient
    private Notificationrecipient createNotificationRecipient(Notification notification, User user, String status) {
        Notificationrecipient recipient = new Notificationrecipient();
        recipient.setNotificationRecipientID(UUID.randomUUID().toString());
        recipient.setNotificationID(notification);
        recipient.setUserID(user);
        recipient.setStatus(status);
        return recipient;
    }


    private void saveNotificationRecipients(Notification notification, List<String> userIds) {
        List<Notificationrecipient> recipients = userIds.stream()
                .map(userId -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid User ID: " + userId));
                    return createNotificationRecipient(notification, user, "UNREAD"); // Sử dụng phương thức tạo
                }).collect(Collectors.toList());
        notificationRecipientRepository.saveAll(recipients);
    }

    public Map<String, Object> markAsRead(String notificationRecipientId) {
        Notificationrecipient recipient = notificationRecipientRepository.findById(notificationRecipientId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Notification Recipient ID: " + notificationRecipientId));
        validateUser(recipient.getUserID().getUsername());
        recipient.setStatus("READ");
        notificationRecipientRepository.save(recipient);
        return buildSuccessResponse("Notification marked as read.");
    }

    @Transactional
    public Map<String, Object> markAllAsRead(Pageable pageable) {
        User user = validateUser(userLoginInfomation.getCurrentUsername());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        // Lấy tất cả Notificationrecipient chưa đọc cho người dùng hiện tại
        List<Notificationrecipient> unreadNotifications = notificationRecipientRepository.findAllByUserID(user.getUserID());

        // Phân trang danh sách các thông báo chưa đọc
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), unreadNotifications.size());
        List<Notificationrecipient> notificationsToMark = unreadNotifications.subList(start, end);

        // Đánh dấu các thông báo trong trang hiện tại là đã đọc
        for (Notificationrecipient recipient : notificationsToMark) {
            recipient.setStatus("READ");
        }
        notificationRecipientRepository.saveAll(notificationsToMark);

        return buildSuccessResponse("All notifications in the current page marked as read.");
    }

    public Page<NotificationRecipientDTO> getNotificationsByUserId(Pageable pageable) {
        User user = validateUser(userLoginInfomation.getCurrentUsername());
        if(user==null){
            throw new UserNotFoundException("User not found");
        }
        List<Notificationrecipient> allRecipients = notificationRecipientRepository.findAllByUserID(user.getUserID());
        return convertToDTOPage(allRecipients, pageable);
    }


    private User validateUser(String username) {
        return userRepository.findByUsername(username);
    }

    private Page<NotificationRecipientDTO> convertToDTOPage(List<Notificationrecipient> recipients, Pageable pageable) {
        recipients.sort(Comparator.comparing(nr -> nr.getNotificationID().getNotificationDate(), Comparator.reverseOrder()));
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), recipients.size());
        List<NotificationRecipientDTO> dtoList = recipients.subList(start, end).stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, recipients.size());
    }

    private NotificationRecipientDTO convertToDTO(Notificationrecipient recipient) {
        return new NotificationRecipientDTO(recipient.getNotificationRecipientID(),
                recipient.getNotificationID().getNotificationID(),
                recipient.getStatus(),
                recipient.getNotificationID().getTitle(),
                recipient.getUserID().getUsername(),
                recipient.getNotificationID().getContent());
    }

    @Transactional
    public Map<String, Object> deleteNotification(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Notification ID: " + notificationId));
        notificationRecipientRepository.deleteByNotificationID(notification);
        notificationRepository.delete(notification);
        return buildSuccessResponse("Notification deleted successfully.");
    }

    private Map<String, Object> buildSuccessResponse(String message) {
        return Map.of("success", true, "message", message);
    }

    private Map<String, Object> buildErrorResponse(String message) {
        return Map.of("success", false, "message", message);
    }

    public Long getUnreadNotificationCount() {
        User user = validateUser(userLoginInfomation.getCurrentUsername());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return notificationRecipientRepository.countUnreadNotifications(user.getUsername());
    }

    @Transactional
    public ResponseEntity<String> deleteNotificationReception(String notificationRecipientID) {
        Notificationrecipient recipient = notificationRecipientRepository.findById(notificationRecipientID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Notification Recipient ID: " + notificationRecipientID));

        // Xóa thông báo theo ID
        notificationRecipientRepository.deleteNotificationReceptionByRecipientID(notificationRecipientID);

        return ResponseEntity.ok("Notification recipient deleted successfully.");
    }
}
