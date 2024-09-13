package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDTO {
    private String orderDetailID;
    private String orderID;
    private String productVersionID;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discountPrice;
}