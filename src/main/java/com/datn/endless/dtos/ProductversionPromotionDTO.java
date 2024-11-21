package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductversionPromotionDTO{
    String productVersionID;
    String productName;
    String versionName;
    BigDecimal purchasePrice;
    BigDecimal price;
    String status;
    String image;
}