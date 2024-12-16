package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EntryDetailDTO {
    private String purchaseOrderDetailID;
    private String productImage;
    private String productName;
    private String productVersionID;
    private String productVersionName;
    private int quantity;
    private BigDecimal price;
}
