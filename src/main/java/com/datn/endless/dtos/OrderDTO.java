package com.datn.endless.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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
    private BigDecimal codValue;
    private BigDecimal insuranceValue;
    private Integer serviceTypeID;
    private BigDecimal totalMoney;
    private String orderAddress;
    private String orderPhone;
    private String orderName;
    private String status;
    List<OrderDetailDTO> orderDetails;
}