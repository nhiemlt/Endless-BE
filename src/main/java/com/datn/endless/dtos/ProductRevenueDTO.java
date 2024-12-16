package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductRevenueDTO {
    private String productName;
    private BigDecimal revenue;
}
