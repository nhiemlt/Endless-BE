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
@Table(name = "promotionproducts")
public class Promotionproduct {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "PromotionProductID", nullable = false, length = 36)
    private String promotionProductID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PromotionID", nullable = false)
    private Promotion promotionID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProductVersionID", nullable = false)
    private Productversion productVersionID;
}