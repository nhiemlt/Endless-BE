package com.datn.endless.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FavoriteDTO {
    private String image;
    private String nameVersionName;
    private BigDecimal purchasePrice;
    private BigDecimal price;

    public FavoriteDTO(String image, String nameVersionName, BigDecimal purchasePrice, BigDecimal price) {
        this.image = image;
        this.nameVersionName = nameVersionName;
        this.purchasePrice = purchasePrice;
        this.price = price;
    }
}
