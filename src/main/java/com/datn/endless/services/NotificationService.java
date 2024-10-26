package com.datn.endless.services;

import com.datn.endless.dtos.NotificationDTO;
import com.datn.endless.dtos.NotificationRecipientDTO;
import com.datn.endless.entities.Notification;
import com.datn.endless.entities.Notificationrecipient;
import com.datn.endless.entities.User;
import com.datn.endless.exceptions.ResourceNotFoundException;
import com.datn.endless.exceptions.UserNotFoundException;
import com.datn.endless.models.NotificationModel;
import com.datn.endless.models.NotificationModelForAll;
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

    public Page<NotificationDTO> getAllNotificationDTOs(String text, String type, Pageable pageable) {
        return findAll(text, type, pageable);
    }

    // Phương thức lấy danh sách thông báo và chuyển đổi thành DTO
    public Page<NotificationDTO> findAll(String text, String type, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findAllNotifications(
                text !=null ? text : "",
                type !=null ? type : "",
                pageable);

        return notifications.map(this::convertToNotificationDTO);
    }

    private NotificationDTO convertToNotificationDTO(Notification notification) {
        return new NotificationDTO(
                notification.getNotificationID(),
                notification.getTitle(),
                notification.getContent(),
                notification.getType(),
                notification.getNotificationDate(),
                notification.getStatus(),
                convertToNotifiRecipientDTO(notification.getNotificationrecipients())
        );
    }

    private List<NotificationRecipientDTO> convertToNotifiRecipientDTO(Set<Notificationrecipient> notificationRecipients) {
        if (notificationRecipients == null) return Collections.emptyList(); // Kiểm tra null

        return notificationRecipients.stream()
                .map(notificationRecipient -> {
                    NotificationRecipientDTO dto = new NotificationRecipientDTO();
                    dto.setContent(notificationRecipient.getNotificationRecipientID());
                    dto.setNotificationID(notificationRecipient.getNotificationID().getNotificationID());
                    dto.setNotificationRecipientID(notificationRecipient.getNotificationRecipientID());
                    dto.setStatus(notificationRecipient.getStatus());
                    dto.setDate(notificationRecipient.getNotificationID().getNotificationDate());
                    dto.setNotificationTitle(notificationRecipient.getNotificationID().getTitle());
                    dto.setUserName(notificationRecipient.getUserID().getUsername());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> sendNotification(@Valid NotificationModel notificationModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildErrorResponse("Lỗi dữ liệu");
        }
        try {
            Notification notification = createNotification(notificationModel);
            notificationRepository.save(notification);
            saveNotificationRecipients(notification, notificationModel.getUserIds());
            return buildSuccessResponse("Thông báo đã được gửi thành công!");
        } catch (Exception e) {
            return buildErrorResponse("Lỗi khi gửi thông báo: " + e.getMessage());
        }
    }

    public void sendNotificationForOrder(@Valid NotificationModelForUser notificationModel) {
        try {
            Notification notification = createNotificationForUser(notificationModel);
            notificationRepository.save(notification);
        } catch (Exception e) {
            throw new IllegalArgumentException("Lỗi khi gửi thông báo: " + e.getMessage());
        }
    }

    public Map<String, Object> sendNotificationForAll(@Valid NotificationModelForAll notificationModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildErrorResponse("Lỗi dữ liệu truyền vào");
        }
        try {
            Notification notification = createNotificationForAll(notificationModel);
            notificationRepository.save(notification);
            return buildSuccessResponse("Thông báo đã được gửi thành công!");
        } catch (Exception e) {
            return buildErrorResponse("Lỗi khi gửi thông báo: " + e.getMessage());
        }
    }

    public Map<String, Object> sendNotificationForAll(@Valid NotificationModelForAll notificationModel) {
        try {
            Notification notification = createNotificationForAll(notificationModel);
            notificationRepository.save(notification);
            return buildSuccessResponse("Thông báo đã được gửi thành công!");
        } catch (Exception e) {
            return buildErrorResponse("Lỗi khi gửi thông báo: " + e.getMessage());
        }
    }


    private Notification createNotification(NotificationModel notificationModel) {
        Notification notification = new Notification();
        notification.setNotificationID(UUID.randomUUID().toString());
        notification.setTitle(notificationModel.getTitle());
        notification.setContent(notificationModel.getContent());
        notification.setType(notificationModel.getType());
        notification.setNotificationDate(Instant.now());
        notification.setStatus("Đã gửi");// Khởi tạo Set nếu cần
        return notification;
    }

    private Notification createNotificationForAll(NotificationModelForAll notificationModel) {
        Notification notification = new Notification();
        notification.setNotificationID(UUID.randomUUID().toString());
        notification.setTitle(notificationModel.getTitle());
        notification.setContent(notificationModel.getContent());
        notification.setType(notificationModel.getType());
        notification.setNotificationDate(Instant.now());
        notification.setStatus("Đã gửi");
        Set<Notificationrecipient> recipients = new HashSet<>();
        for(User user: userRepository.findAll()) {
            Notificationrecipient notificationrecipient = new Notificationrecipient();
            notificationrecipient.setNotificationRecipientID(UUID.randomUUID().toString());
            notificationrecipient.setNotificationID(notification);
            notificationrecipient.setUserID(user);
            notificationrecipient.setStatus("UNREAD");
            recipients.add(notificationrecipient);
        }
        notification.setNotificationrecipients(recipients);
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
        notification.setStatus("Đã gửi");

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

    public List<NotificationRecipientDTO> getNotificationsByUserId() {
        User user = validateUser(userLoginInfomation.getCurrentUsername());
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        List<Notificationrecipient> allRecipients = notificationRecipientRepository.findAllByUserID(user.getUserID());
        return convertToDTOList(allRecipients);
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

    private List<NotificationRecipientDTO> convertToDTOList(List<Notificationrecipient> recipients) {
        // Sắp xếp danh sách trước khi chuyển đổi sang DTO
        recipients.sort(Comparator.comparing(nr -> nr.getNotificationID().getNotificationDate(), Comparator.reverseOrder()));
        return recipients.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NotificationRecipientDTO convertToDTO(Notificationrecipient recipient) {
        return new NotificationRecipientDTO(
                recipient.getNotificationRecipientID(),
                recipient.getNotificationID().getNotificationID(),
                recipient.getStatus(),
                recipient.getNotificationID().getNotificationDate(),
                recipient.getNotificationID().getTitle(),
                recipient.getNotificationID().getContent(),
                recipient.getUserID().getUsername());
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
