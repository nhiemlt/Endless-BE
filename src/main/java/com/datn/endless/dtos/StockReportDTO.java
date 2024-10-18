package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReportDTO {
    private String versionName;
    private Long totalEntryQuantity;
    private Long totalOrderQuantity;
}

