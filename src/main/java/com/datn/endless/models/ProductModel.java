package com.datn.endless.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductModel {
    @NotNull
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String nameEn;

    private String description;
    private String enDescription;

    @NotNull
    private String categoryID;

    @NotNull
    private String brandID;
}
