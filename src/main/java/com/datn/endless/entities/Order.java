package com.datn.endless.entities;

import jakarta.persistence.*;
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
@Table(name = "orders")
public class Order {
    private String orderID;

    private User userID;

    private Voucher voucherID;

    private LocalDate orderDate;

    private BigDecimal totalMoney;

    private String orderStatus;

    private String orderAddress;

    private String orderPhone;

    private String orderName;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "OrderID", nullable = false, length = 36)
    public String getOrderID() {
        return orderID;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    public User getUserID() {
        return userID;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VoucherID")
    public Voucher getVoucherID() {
        return voucherID;
    }

    @NotNull
    @Column(name = "OrderDate", nullable = false)
    public LocalDate getOrderDate() {
        return orderDate;
    }

    @NotNull
    @Column(name = "TotalMoney", nullable = false, precision = 18, scale = 2)
    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "OrderStatus", nullable = false, length = 50)
    public String getOrderStatus() {
        return orderStatus;
    }

    @Lob
    @Column(name = "OrderAddress")
    public String getOrderAddress() {
        return orderAddress;
    }

    @Size(max = 15)
    @Column(name = "OrderPhone", length = 15)
    public String getOrderPhone() {
        return orderPhone;
    }

    @Size(max = 255)
    @Column(name = "OrderName")
    public String getOrderName() {
        return orderName;
    }

}