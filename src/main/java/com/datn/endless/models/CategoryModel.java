package com.datn.endless.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryModel {
    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 255)
    private String name;

}
