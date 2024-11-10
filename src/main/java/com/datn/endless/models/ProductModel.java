package com.datn.endless.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductModel {

    @NotBlank(message = "Tên sản phẩm không được để trống.")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự.")
    private String name;

    @Size(max = 500, message = "Mô tả sản phẩm không được vượt quá 500 ký tự.")
    private String description;

    @NotBlank(message = "Danh mục không được để trống.")
    private String categoryID;

    @NotBlank(message = "Thương hiệu không được để trống.")
    private String brandID;
}
