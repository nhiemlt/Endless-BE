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
@Table(name = "orderdetails")
public class Orderdetail {
    private String orderDetailID;

    private Order orderID;

    private Productversion productVersionID;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal discountPrice;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "OrderDetailID", nullable = false, length = 36)
    public String getOrderDetailID() {
        return orderDetailID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OrderID", nullable = false)
    public Order getOrderID() {
        return orderID;
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

    @NotNull
    @Column(name = "DiscountPrice", nullable = false, precision = 18, scale = 2)
    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

}