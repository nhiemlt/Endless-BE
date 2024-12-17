package com.datn.endless.dtos;

import lombok.*;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatisticsDTO {
    private String categoryName;
    private BigDecimal totalRevenue;
    private Double percentage;
}
