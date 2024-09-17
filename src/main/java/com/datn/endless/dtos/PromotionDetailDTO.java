package com.datn.endless.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionDetailDTO {
    private String promotionDetailID;
    private String promotionID;
    private Integer percentDiscount;
}
