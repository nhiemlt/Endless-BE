package com.datn.endless.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryModel {
    @NotNull
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String enName;
}
