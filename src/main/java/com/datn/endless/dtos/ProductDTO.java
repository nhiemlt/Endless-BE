package com.datn.endless.dtos;


import lombok.Data;


@Data
public class ProductDTO {
    private String productID;
    private CategoryDTO categoryID;
    private BrandDTO brandID;
    private String name;
    private String description;
}
