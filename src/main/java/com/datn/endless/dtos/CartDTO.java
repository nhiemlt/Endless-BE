package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartDTO {
    private String cartID;
    private String productVersionID;
    private String productName;
    private String versionName;
    private String image;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer quantity;
    private Integer weight;
    private Integer height;
    private Integer length;
    private Integer width;
}
