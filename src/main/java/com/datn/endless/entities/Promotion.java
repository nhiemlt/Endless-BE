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
@Table(name = "promotions")
public class Promotion {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "PromotionID", nullable = false, length = 36)
    private String promotionID;

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "StartDate", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "EndDate", nullable = false)
    private Instant endDate;

    @NotNull
    @Column(name = "PercentDiscount", nullable = false)
    private Integer percentDiscount;

    @Lob
    @Column(name = "Poster")
    private String poster;

    @ColumnDefault("1")
    @Column(name = "Active")
    private Boolean active;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "CreateDate")
    private Instant createDate;

    @OneToMany(mappedBy = "promotionID", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Promotionproduct> promotionproducts = new LinkedHashSet<>();

}