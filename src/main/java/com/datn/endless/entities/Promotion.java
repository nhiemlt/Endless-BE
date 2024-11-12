package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "PromotionID", nullable = false, length = 36)
    private String promotionID;


    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;


    @NotNull
    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

    @Lob
    @Column(name = "Poster")
    private String poster;
    
    // Quan hệ với Promotiondetail
    @OneToMany(mappedBy = "promotionID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Promotiondetail> promotionDetails; // Kiểu List<Promotiondetail>

}