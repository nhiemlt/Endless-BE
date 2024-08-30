package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "notificationrecipients")
public class Notificationrecipient {
    private String notificationRecipientID;

    private Notification notificationID;

    private User userID;

    private String status;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "NotificationRecipientID", nullable = false, length = 36)
    public String getNotificationRecipientID() {
        return notificationRecipientID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NotificationID", nullable = false)
    public Notification getNotificationID() {
        return notificationID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    public User getUserID() {
        return userID;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "Status", nullable = false, length = 50)
    public String getStatus() {
        return status;
    }

}