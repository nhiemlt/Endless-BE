package com.datn.endless.controllers;

import com.datn.endless.dtos.NotificationDTO;
import com.datn.endless.dtos.NotificationRecipientDTO;
import com.datn.endless.entities.Notification;
import com.datn.endless.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody @Valid NotificationDTO notificationDTO, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body("Invalid input: " + errorMessage);
        }
        notificationService.sendNotification(notificationDTO);
        return ResponseEntity.ok("Notification sent successfully!");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationRecipientDTO>> getNotificationsByUserId(@PathVariable String userId) {
        List<NotificationRecipientDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }


    @PostMapping("/markAsRead/{notificationRecipientId}")
    public ResponseEntity<String> markAsRead(@PathVariable String notificationRecipientId) {
        notificationService.markAsRead(notificationRecipientId);
        return ResponseEntity.ok("Notification marked as read.");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted.");
    }
}
