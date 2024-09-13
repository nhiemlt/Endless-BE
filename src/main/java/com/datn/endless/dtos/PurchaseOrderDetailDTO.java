package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderDetailDTO {
    private String productVersionID;
    private int quantity;
    private BigDecimal price;
}
