package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "NotificationID", nullable = false, length = 36)
    private String notificationID;

    @Size(max = 255)
    @NotNull
    @Column(name = "Title", nullable = false)
    private String title;

    @NotNull
    @Lob
    @Column(name = "Content", nullable = false)
    private String content;

    @Size(max = 50)
    @NotNull
    @Column(name = "Type", nullable = false, length = 50)
    private String type;

    @NotNull
    @Column(name = "NotificationDate", nullable = false)
    private Instant notificationDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Status", nullable = false, length = 50)
    private String status;

    @OneToMany(mappedBy = "notificationID")
    private Set<Notificationrecipient> notificationrecipients = new LinkedHashSet<>();

}