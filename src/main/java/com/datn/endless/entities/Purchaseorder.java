package com.datn.endless.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "purchaseorders")
public class Purchaseorder {
    private String purchaseOrderID;

    private LocalDate purchaseDate;

    private String purchaseOrderStatus;

    private BigDecimal totalMoney;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "PurchaseOrderID", nullable = false, length = 36)
    public String getPurchaseOrderID() {
        return purchaseOrderID;
    }

    @NotNull
    @Column(name = "PurchaseDate", nullable = false)
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "PurchaseOrderStatus", nullable = false, length = 50)
    public String getPurchaseOrderStatus() {
        return purchaseOrderStatus;
    }

    @NotNull
    @Column(name = "TotalMoney", nullable = false, precision = 18, scale = 2)
    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

}