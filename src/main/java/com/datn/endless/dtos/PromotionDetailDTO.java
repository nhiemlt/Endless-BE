package com.datn.endless.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PromotionDetailDTO {
    private String promotionDetailID;
    private String promotionID;
    private Integer percentDiscount;
    private List<PromotionproductDTO> promotionProducts = new ArrayList<>();
}
