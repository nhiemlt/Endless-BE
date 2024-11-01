package com.datn.endless.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PromotionDetailModel {
    @NotNull
    @Size(max = 36)
    private String promotionID;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @Min(value = 1, message = "Phần trăm giảm giá phải lớn hơn 0") // Thêm annotation kiểm tra
    @Max(value = 100, message = "Phần trăm giảm giá không được vượt quá 100") // Kiểm tra không vượt quá 100
    private Integer percentDiscount;
}
