package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class OrderDetailDTO {
    private String productNameVersionName; // Tên sản phẩm cộng với tên phiên bản sản phẩm
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String orderStatus;
    private LocalDate orderDate;
}