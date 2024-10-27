package com.datn.endless.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class CartModel {
    @NotBlank(message = "Id của phiên bản sản phẩm không được trống")
    private String productVersionID;

    @Min(value = 1, message = "Số lượng phải lớn hơn 1")
    private Integer quantity;
}
