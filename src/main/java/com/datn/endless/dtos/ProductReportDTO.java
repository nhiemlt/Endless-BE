package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductReportDTO {
    private String productVersionName;
    private Long totalQuantitySold;
}