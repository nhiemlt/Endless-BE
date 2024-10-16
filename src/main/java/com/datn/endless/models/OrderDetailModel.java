package com.datn.endless.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDetailModel {

    @NotBlank(message = "Product Version ID cannot be empty")
    private String productVersionID;

    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;
}
