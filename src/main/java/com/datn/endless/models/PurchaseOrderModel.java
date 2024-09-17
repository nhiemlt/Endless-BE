package com.datn.endless.models;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseOrderModel {
    private List<PurchaseOrderDetailModel> details;
}
