package com.datn.endless.models;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VoucherModel {

    @NotNull(message = "Mã voucher không được để trống")
    @NotBlank(message = "Mã voucher không được để trống")
    private String voucherCode;

    @NotNull(message = "Hóa đơn tối thiểu không được để trống")
    @DecimalMin(value = "1.000", inclusive = true, message = "Hóa đơn tối thiểu phải lớn hơn hoặc bằng 0")
    private BigDecimal leastBill;

    @NotNull(message = "Giá trị giảm tối thiểu không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá trị giảm tối thiểu phải lớn hơn hoặc bằng 0")
    private BigDecimal leastDiscount;

    @NotNull(message = "Giá trị giảm tối đa không được để trống")
    @DecimalMin(value = "1.000", inclusive = true, message = "Giá trị giảm tối đa phải lớn hơn hoặc bằng 0")
    private BigDecimal biggestDiscount;

    @NotNull(message = "Mức giảm giá không được để trống")
    @Min(value = 1, message = "Mức giảm giá phải ít nhất là 1")
    @Max(value = 50, message = "Mức giảm giá phải không được vượt quá 50")
    private Integer discountLevel;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

}