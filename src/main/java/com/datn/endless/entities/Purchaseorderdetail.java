package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "purchaseorderdetails")
public class Purchaseorderdetail {
    private String purchaseOrderDetailID;

    private Purchaseorder purchaseOrderID;

    private Productversion productVersionID;

    private Integer quantity;

    private BigDecimal price;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "PurchaseOrderDetailID", nullable = false, length = 36)
    public String getPurchaseOrderDetailID() {
        return purchaseOrderDetailID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PurchaseOrderID", nullable = false)
    public Purchaseorder getPurchaseOrderID() {
        return purchaseOrderID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProductVersionID", nullable = false)
    public Productversion getProductVersionID() {
        return productVersionID;
    }

    @NotNull
    @Column(name = "Quantity", nullable = false)
    public Integer getQuantity() {
        return quantity;
    }

    @NotNull
    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    public BigDecimal getPrice() {
        return price;
    }

}