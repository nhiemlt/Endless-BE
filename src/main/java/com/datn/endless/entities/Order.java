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
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Size(max = 36)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @ColumnDefault("(uuid())")
    @Column(name = "OrderID", nullable = false, length = 36)
    private String orderID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserID", nullable = false)
    private User userID;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Creater", nullable = true) // Sử dụng cột khác
    private User creater;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VoucherID")
    private Voucher voucherID;

    @NotNull
    @Column(name = "OrderDate", nullable = false)
    private LocalDate orderDate;

    @NotNull
    @Column(name = "TotalMoney", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalMoney;

    @Lob
    @Column(name = "OrderAddress")
    private String orderAddress;

    @Size(max = 15)
    @Column(name = "OrderPhone", length = 15)
    private String orderPhone;

    @Size(max = 255)
    @Column(name = "OrderName")
    private String orderName;

    @OneToMany(mappedBy = "orderID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Orderdetail> orderDetails;
}