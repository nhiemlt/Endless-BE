package com.datn.endless.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderDetailDTO {
    private String orderDetailID;
    private String orderID;
    private String productVersionID;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discountPrice;
}