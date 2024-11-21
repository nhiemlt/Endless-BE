package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionproductDTO{
    String promotionProductID;
    ProductversionPromotionDTO productVersionID;
}