package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class PurchaseOrderDTO {
    private LocalDate purchaseDate;
    private String purchaseOrderStatus;
    private BigDecimal totalMoney;
    private List<PurchaseOrderDetailDTO> details;
}
