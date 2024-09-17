package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "promotions")
public class Promotion {
    @Id
    @Size(max = 36)
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.uuid.UuidGenerator")
    @Column(name = "PromotionID", nullable = false, length = 36)
    private String promotionID;

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "EN_name")
    private String enName;

    @NotNull
    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

    @Size(max = 255)
    @Column(name = "Poster")
    private String poster;

    @Lob
    @Column(name = "EN_description")
    private String enDescription;

}