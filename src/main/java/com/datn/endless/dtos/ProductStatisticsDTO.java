package com.datn.endless.dtos;

import lombok.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatisticsDTO {
    private String productName;
    private String productVersion;
    private String productImage;
    private Long totalImport;
    private Long totalSales;
    private BigDecimal totalRevenue;
    private BigDecimal importPrice;
    private BigDecimal exportPrice;
}
