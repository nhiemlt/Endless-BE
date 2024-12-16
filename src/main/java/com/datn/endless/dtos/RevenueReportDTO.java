package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class RevenueReportDTO {
    private BigDecimal totalRevenue;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalOrders;
    private int totalProductsSold;
    private List<Detail> details;

    @Data
    @AllArgsConstructor
    public static class Detail {
        private LocalDate month;
        private BigDecimal monthlyRevenue;
    }
}
