package com.datn.endless.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
public class OrderModel {
    @NotBlank(message = "User ID cannot be empty")
    private String userID;

    private String voucherID;
    
    private String creater;

    @NotBlank(message = "Order address cannot be empty")
    private String orderAddress;

    @NotBlank(message = "Order phone cannot be empty")
    private String orderPhone;

    @NotBlank(message = "Order name cannot be empty")
    private String orderName;

    private List<OrderDetailModel> orderDetails;
}