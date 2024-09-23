package com.datn.endless.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderDetailModel {
    private String productVersionID;
    private int quantity;
}
