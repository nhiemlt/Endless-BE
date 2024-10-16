package com.datn.endless.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ReportID", length = 36)
    private String reportID;

    @Column(name = "Title", nullable = false)
    private String title;

    @Lob
    @Column(name = "Description")
    private String description;

    @Column(name = "CreationDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "CreatedBy", nullable = false)
    private String createdBy;

    @Column(name = "IsActive", nullable = false)
    private Boolean isActive;
}
