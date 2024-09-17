package com.datn.endless.dtos;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class NotificationDTO {
    private String notificationID;
    private String title;
    private String content;
    private String type;
    private Instant notificationDate;
    private String status;
}

