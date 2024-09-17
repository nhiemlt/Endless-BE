package com.datn.endless.dtos;

import lombok.Data;

@Data
public class NotificationRecipientDTO {
    private String notificationRecipientID;
    private String notificationID;
    private String status;
    private String notificationTitle;
    private String userName;
}
