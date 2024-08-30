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
@Table(name = "promotiondetails")
public class Promotiondetail {
    private String promotionDetailID;

    private Promotion promotionID;

    private Integer percentDiscount;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "PromotionDetailID", nullable = false, length = 36)
    public String getPromotionDetailID() {
        return promotionDetailID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PromotionID", nullable = false)
    public Promotion getPromotionID() {
        return promotionID;
    }

    @NotNull
    @Column(name = "PercentDiscount", nullable = false)
    public Integer getPercentDiscount() {
        return percentDiscount;
    }

}