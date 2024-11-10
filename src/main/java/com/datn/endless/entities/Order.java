package com.datn.endless.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
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
    private LocalDateTime orderDate;

    @NotNull
    @Column(name = "ShipFee", nullable = false, precision = 18, scale = 2)
    private BigDecimal shipFee;

    @Column(name = "VoucherDiscount", nullable = false, precision = 18, scale = 2)
    private BigDecimal voucherDiscount;

    @NotNull
    @Column(name = "TotalMoney", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalMoney;

    @ColumnDefault("0.00")
    @Column(name = "CodValue", precision = 18, scale = 2)
    private BigDecimal codValue;

    @ColumnDefault("0.00")
    @Column(name = "InsuranceValue", precision = 18, scale = 2)
    private BigDecimal insuranceValue;

    @NotNull
    @Column(name = "ServiceTypeID", nullable = false)
    private Integer serviceTypeID;

    @Lob
    @Column(name = "OrderAddress")
    private String orderAddress;

    @Size(max = 15)
    @Column(name = "OrderPhone", length = 15)
    private String orderPhone;

    @Size(max = 255)
    @Column(name = "OrderName")
    private String orderName;

    @OneToMany(mappedBy = "orderID", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Orderdetail> orderdetails = new LinkedHashSet<>();

}