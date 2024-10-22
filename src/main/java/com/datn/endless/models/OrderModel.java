package com.datn.endless.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderModel {
    private String voucherID;

    @NotBlank(message = "Order address cannot be empty")
    private String orderAddress;

    @NotBlank(message = "Order phone cannot be empty")
    private String orderPhone;

    @NotBlank(message = "Order name cannot be empty")
    private String orderName;

    private BigDecimal shipFee = BigDecimal.valueOf(0);

    private List<OrderDetailModel> orderDetails;
}