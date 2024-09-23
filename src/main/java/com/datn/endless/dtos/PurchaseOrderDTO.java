package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class PurchaseOrderDTO {
    private String purchaseOrderID;
    private LocalDate purchaseDate;
    private BigDecimal totalMoney;
    private List<PurchaseOrderDetailDTO> details;
}