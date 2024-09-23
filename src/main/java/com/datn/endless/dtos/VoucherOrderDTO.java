package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VoucherOrderDTO {
    private String voucherID;
    private String voucherCode;
    private BigDecimal voucherAmount;
}
