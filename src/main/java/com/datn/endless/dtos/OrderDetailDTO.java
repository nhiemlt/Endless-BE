package com.datn.endless.dtos;

import lombok.*;

import java.math.BigDecimal;

@Data
public class OrderDetailDTO {
    private String orderDetailID;
    private String orderID;
    private String productName;
    private String productVersionID;
    private String productVersionName;
    private String productVersionImage;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private boolean rated;
}