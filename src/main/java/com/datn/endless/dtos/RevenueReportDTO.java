package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RevenueReportDTO {
    private BigDecimal totalRevenue; // Tổng doanh thu
    private LocalDate startDate; // Ngày bắt đầu
    private LocalDate endDate; // Ngày kết thúc

    // Constructor chỉ với tổng doanh thu
    public RevenueReportDTO(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
