package com.datn.endless.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDTO {
    private String orderID;
    private UserOderDTO customer;
    private VoucherOrderDTO voucher;
    private LocalDate orderDate;
    private BigDecimal shipFee;
    private BigDecimal totalMoney;
    private String orderAddress;
    private String orderPhone;
    private String orderName;
    private String status;
    List<OrderDetailDTO> orderDetails;
}