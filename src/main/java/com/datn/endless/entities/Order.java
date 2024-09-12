package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Size(max = 36)
//    @ColumnDefault("(uuid())")
    @Column(name = "OrderID", nullable = false, length = 36)
    private String orderID;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private User userID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VoucherID")
    private Voucher voucherID;

    @NotNull
    @Column(name = "OrderDate", nullable = false)
    private LocalDate orderDate;

    @NotNull
    @Column(name = "TotalMoney", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalMoney;

    @Size(max = 50)
    @NotNull
    @Column(name = "OrderStatus", nullable = false, length = 50)
    private String orderStatus;

    @Lob
    @Column(name = "OrderAddress")
    private String orderAddress;

    @Size(max = 15)
    @Column(name = "OrderPhone", length = 15)
    private String orderPhone;

    @Size(max = 255)
    @Column(name = "OrderName")
    private String orderName;

}