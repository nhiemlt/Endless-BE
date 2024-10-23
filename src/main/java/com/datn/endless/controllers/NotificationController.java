package com.datn.endless.controllers;

import com.datn.endless.dtos.NotificationRecipientDTO;
import com.datn.endless.models.NotificationModel;
import com.datn.endless.models.NotificationModelForAll;
import com.datn.endless.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendNotification(
            @Valid @RequestBody NotificationModel notificationModel,
            BindingResult bindingResult) {
        Map<String, Object> response = notificationService.sendNotification(notificationModel, bindingResult);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-all")
    public ResponseEntity<Map<String, Object>> sendNotificationForAll(
            @Valid @RequestBody NotificationModelForAll notificationModel,
            BindingResult bindingResult) {
        Map<String, Object> response = notificationService.sendNotificationForAll(notificationModel, bindingResult);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationRecipientDTO>> getNotificationsByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "NotificationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<NotificationRecipientDTO> notifications = notificationService.getNotificationsByUserId(pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<NotificationRecipientDTO>> getNotificationsByUserLogin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "NotificationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<NotificationRecipientDTO> notifications = notificationService.getNotificationsByUserId(pageable);
        return ResponseEntity.ok(notifications);
    }


    @PostMapping("/markAsRead")
    public ResponseEntity<Map<String, Object>> markAsRead(@RequestBody Map<String, String> requestBody) {
        String notificationRecipientId = requestBody.get("notificationRecipientId");
        Map<String, Object> response = notificationService.markAsRead(notificationRecipientId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/markAllAsRead")
    public ResponseEntity<Map<String, Object>> markAllAsRead(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "NotificationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Map<String, Object> response = notificationService.markAllAsRead(pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteNotification(@RequestBody Map<String, String> requestBody) {
        String notificationId = requestBody.get("notificationId");
        Map<String, Object> response = notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount() {
        Long unreadCount = notificationService.getUnreadNotificationCount();
        return ResponseEntity.ok(unreadCount);
    }

    @DeleteMapping("/delete/{notificationRecipientID}")
    public ResponseEntity<String> deleteNotificationReception(@PathVariable String notificationRecipientID) {
        return notificationService.deleteNotificationReception(notificationRecipientID);
    }

}