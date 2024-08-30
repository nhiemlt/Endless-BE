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
    private String voucherID;

    private String voucherCode;

    private BigDecimal leastBill;

    private BigDecimal leastDiscount;

    private BigDecimal biggestDiscount;

    private Integer discountLevel;

    private String discountForm;

    private LocalDate startDate;

    private LocalDate endDate;

    @Id
    @Size(max = 36)
    @ColumnDefault("(uuid())")
    @Column(name = "VoucherID", nullable = false, length = 36)
    public String getVoucherID() {
        return voucherID;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "VoucherCode", nullable = false, length = 50)
    public String getVoucherCode() {
        return voucherCode;
    }

    @NotNull
    @Column(name = "LeastBill", nullable = false, precision = 18, scale = 2)
    public BigDecimal getLeastBill() {
        return leastBill;
    }

    @NotNull
    @Column(name = "LeastDiscount", nullable = false, precision = 18, scale = 2)
    public BigDecimal getLeastDiscount() {
        return leastDiscount;
    }

    @NotNull
    @Column(name = "BiggestDiscount", nullable = false, precision = 18, scale = 2)
    public BigDecimal getBiggestDiscount() {
        return biggestDiscount;
    }

    @NotNull
    @Column(name = "DiscountLevel", nullable = false)
    public Integer getDiscountLevel() {
        return discountLevel;
    }

    @Size(max = 50)
    @NotNull
    @Column(name = "DiscountForm", nullable = false, length = 50)
    public String getDiscountForm() {
        return discountForm;
    }

    @NotNull
    @Column(name = "StartDate", nullable = false)
    public LocalDate getStartDate() {
        return startDate;
    }

    @NotNull
    @Column(name = "EndDate", nullable = false)
    public LocalDate getEndDate() {
        return endDate;
    }

}