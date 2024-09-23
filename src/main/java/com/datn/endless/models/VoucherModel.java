package com.datn.endless.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VoucherModel {

    @NotNull(message = "voucherCode must not be empty")
    private String voucherCode;

    @NotNull(message = "leastBill must not be empty")
    @DecimalMin(value = "0.0", inclusive = true, message = "leastBill must be non-negative")
    private BigDecimal leastBill;

    @NotNull(message = "leastDiscount must not be empty")
    @DecimalMin(value = "0.0", inclusive = true, message = "leastDiscount must be non-negative")
    private BigDecimal leastDiscount;

    @NotNull(message = "biggestDiscount must not be empty")
    @DecimalMin(value = "0.0", inclusive = true, message = "biggestDiscount must be non-negative")
    private BigDecimal biggestDiscount;

    @NotNull(message = "discountLevel must not be empty")
    @Min(value = 0, message = "discountLevel must be at least 0")
    @Max(value = 50, message = "discountLevel must be at most 50")
    private Integer discountLevel;

    private String discountForm;

    @NotNull(message = "startDate must not be empty")
    private LocalDate startDate;

    @NotNull(message = "endDate must not be empty")
    private LocalDate endDate;
}
