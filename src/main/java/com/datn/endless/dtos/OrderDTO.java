package com.datn.endless.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class OrderDTO {
    private String orderID;
    private String userID;
    private String voucherID;
    private LocalDate orderDate;
    private BigDecimal totalMoney;
    private String orderStatus;
    private String orderAddress;
    private String orderPhone;
    private String orderName;
}