package com.datn.endless.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private String orderID;
    private UserOderDTO customer;
    private String voucher;
    private BigDecimal voucherDiscount;
    private LocalDateTime orderDate;
    private BigDecimal shipFee;
    private BigDecimal codValue;
    private BigDecimal insuranceValue;
    private Integer serviceTypeID;
    private BigDecimal totalProductPrice;
    private BigDecimal money;
    private BigDecimal totalMoney;
    private String orderAddress;
    private String orderPhone;
    private String orderName;
    private String status;
    List<OrderDetailDTO> orderDetails;
}