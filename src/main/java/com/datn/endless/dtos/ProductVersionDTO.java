package com.datn.endless.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductVersionDTO {
    private String productVersionID;
    private String productName;
    private String versionName;
    private BigDecimal purchasePrice;
    private BigDecimal price;
    private String status;
    private String image;
}
