package com.datn.endless.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CartModel {
    @NotBlank(message = "Product Version ID cannot be empty")
    private String productVersionID;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
