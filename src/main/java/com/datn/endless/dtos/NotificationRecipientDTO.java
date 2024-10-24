package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecipientDTO {
    private String notificationRecipientID;
    private String notificationID;
    private String status;
    private Instant date;
    private String notificationTitle;
    private String content;
    private String userName;
}
