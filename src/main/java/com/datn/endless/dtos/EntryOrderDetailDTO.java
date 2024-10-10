package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EntryOrderDetailDTO {
    private String purchaseOrderDetailID;
    private String productVersionID;
    private String productVersionName;
    private int quantity;
    private BigDecimal price;
}
