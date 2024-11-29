package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VoucherDTO {
    private String voucherID;
    private String voucherCode;
    private BigDecimal leastBill;
    private BigDecimal leastDiscount;
    private BigDecimal biggestDiscount;
    private Integer discountLevel;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
