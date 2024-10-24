package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String notificationID;
    private String title;
    private String content;
    private String type;
    private Instant notificationDate;
    private String status;
    private List<NotificationRecipientDTO> notifications;
}

