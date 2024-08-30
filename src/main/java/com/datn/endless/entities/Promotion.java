package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "promotions")
public class Promotion {
    private String promotionID;

    private String name;

    private String enName;

    private LocalDate startDate;

    private LocalDate endDate;

    private String poster;

    private String enDescription;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "PromotionID", nullable = false, length = 36)
    public String getPromotionID() {
        return promotionID;
    }

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    public String getName() {
        return name;
    }

    @Size(max = 255)
    @Column(name = "EN_name")
    public String getEnName() {
        return enName;
    }

    @NotNull
    @Column(name = "StartDate", nullable = false)
    public LocalDate getStartDate() {
        return startDate;
    }

    @NotNull
    @Column(name = "EndDate", nullable = false)
    public LocalDate getEndDate() {
        return endDate;
    }

    @Size(max = 255)
    @Column(name = "Poster")
    public String getPoster() {
        return poster;
    }

    @Lob
    @Column(name = "EN_description")
    public String getEnDescription() {
        return enDescription;
    }

}