package com.datn.endless.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PromotionDetailModel {
    @NotNull
    @Size(max = 36)
    private String promotionID;

    @NotNull
    private Integer percentDiscount;
}
