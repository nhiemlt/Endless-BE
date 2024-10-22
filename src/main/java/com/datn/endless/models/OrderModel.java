package com.datn.endless.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

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

    private BigDecimal codValue = BigDecimal.valueOf(0);

    private BigDecimal insuranceValue = BigDecimal.valueOf(0);

    @NotNull(message = "Order name cannot be empty")
    private Integer serviceTypeID;

    private List<OrderDetailModel> orderDetails;
}