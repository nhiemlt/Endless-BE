package com.datn.endless.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVersionModel {

    @NotNull(message = "Product ID cannot be null.")
    private String productID;

    @Size(max = 255, message = "Version name cannot exceed 255 characters.")
    @NotNull(message = "Version name cannot be null.")
    private String versionName;

    private String description;

    @NotNull(message = "Purchase price cannot be null.")
    private BigDecimal purchasePrice;

    @NotNull(message = "Price cannot be null.")
    private BigDecimal price;

    @NotNull(message = "Image cannot be null.")
    private MultipartFile image; // Chỉnh sửa ở đây

    @NotNull(message = "Please choose some Attributes.")
    private List<String> attributeValueID;
}
