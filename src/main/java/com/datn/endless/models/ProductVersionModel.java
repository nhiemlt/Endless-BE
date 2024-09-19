package com.datn.endless.models;

import com.datn.endless.entities.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVersionModel {

    @NotNull(message = "Product ID cannot be null.")
    private Product productID;

    @Size(max = 255, message = "Version name cannot exceed 255 characters.")
    @NotNull(message = "Version name cannot be null.")
    private String versionName;

    @NotNull(message = "Purchase price cannot be null.")
    private BigDecimal purchasePrice;

    @NotNull(message = "Price cannot be null.")
    private BigDecimal price;

    @NotNull(message = "Image cannot be null.")
    private String image;

    @NotNull(message = "please chose some Attributes")
    List<String> AttributeID ;
}
