package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {
    private String notificationID;

    private String title;

    private String content;

    private String type;

    private Instant notificationDate;

    private String status;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "NotificationID", nullable = false, length = 36)
    public String getNotificationID() {
        return notificationID;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "Title", nullable = false)
    public String getTitle() {
        return title;
    }

    @NotNull
    @Lob
    @Column(name = "Content", nullable = false)
    public String getContent() {
        return content;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "Type", nullable = false, length = 50)
    public String getType() {
        return type;
    }

    @NotNull
    @Column(name = "NotificationDate", nullable = false)
    public Instant getNotificationDate() {
        return notificationDate;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "Status", nullable = false, length = 50)
    public String getStatus() {
        return status;
    }

}