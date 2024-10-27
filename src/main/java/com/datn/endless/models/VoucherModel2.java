package com.datn.endless.models;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class VoucherModel2 {

    @NotNull(message = "Mã voucher không được để trống")
    private String voucherCode;

    @NotNull(message = "Hóa đơn tối thiểu không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Hóa đơn tối thiểu phải lớn hơn hoặc bằng 0")
    private BigDecimal leastBill;

    @NotNull(message = "Giá trị giảm tối thiểu không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá trị giảm tối thiểu phải lớn hơn hoặc bằng 0")
    private BigDecimal leastDiscount;

    @NotNull(message = "Giá trị giảm tối đa không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá trị giảm tối đa phải lớn hơn hoặc bằng 0")
    private BigDecimal biggestDiscount;

    @NotNull(message = "Mức giảm giá không được để trống")
    @Min(value = 0, message = "Mức giảm giá phải ít nhất là 0")
    @Max(value = 50, message = "Mức giảm giá phải không được vượt quá 50")
    private Integer discountLevel;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    @NotNull(message = "Danh sách người dùng không được để trống")
    private List<String> userIds;

}
