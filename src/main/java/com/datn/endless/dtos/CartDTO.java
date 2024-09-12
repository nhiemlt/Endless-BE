package com.datn.endless.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartDTO {
    private String image;
    private String versionName;
    private BigDecimal purchasePrice;
    private BigDecimal price;
    private Integer quantity;

    public CartDTO(String image, String versionName, BigDecimal purchasePrice, BigDecimal price, Integer quantity) {
        this.image = image;
        this.versionName = versionName;
        this.purchasePrice = purchasePrice;
        this.price = price;
        this.quantity = quantity;
    }
}
