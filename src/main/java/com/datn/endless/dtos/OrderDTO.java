package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String orderID;
    private String userID;
    private String voucherID;
    private LocalDate orderDate;
    private BigDecimal totalMoney;
    private String orderAddress;
    private String orderPhone;
    private String orderName;
    List<OrderDetailDTO> orderDetails;

    public OrderDTO(String orderID, String userID, String voucherID, String orderAddress, List<OrderDetailDTO> orderDetails ){
        this.orderID = orderID;
        this.userID = userID;
        this.voucherID = voucherID;
        this.orderAddress = orderAddress;
        this.orderDetails = orderDetails;
    };
}