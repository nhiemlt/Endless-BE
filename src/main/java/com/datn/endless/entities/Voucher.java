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
@Table(name = "vouchers")
public class Voucher {
    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "VoucherID", nullable = false, length = 36)
    private String voucherID;

    @Size(max = 50)
    @NotNull
    @Column(name = "VoucherCode", nullable = false, length = 50)
    private String voucherCode;

    @NotNull
    @Column(name = "LeastBill", nullable = false, precision = 18, scale = 2)
    private BigDecimal leastBill;

    @NotNull
    @Column(name = "LeastDiscount", nullable = false, precision = 18, scale = 2)
    private BigDecimal leastDiscount;

    @NotNull
    @Column(name = "BiggestDiscount", nullable = false, precision = 18, scale = 2)
    private BigDecimal biggestDiscount;

    @NotNull
    @Column(name = "DiscountLevel", nullable = false)
    private Integer discountLevel;

    @Size(max = 50)
    @NotNull
    @Column(name = "DiscountForm", nullable = false, length = 50)
    private String discountForm;

    @NotNull
    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

}




