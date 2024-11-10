package com.datn.endless.models;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVersionModel {

    private String productID;

    @NotNull(message = "Tên phiên bản không được để trống.")
    @Size(max = 255, message = "Tên phiên bản không được vượt quá 255 ký tự.")
    private String versionName;

    @NotNull(message = "Giá nhập không được để trống.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá nhập phải lớn hơn 0.")
    private BigDecimal purchasePrice;

    @NotNull(message = "Giá bán không được để trống.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0.")
    private BigDecimal price;

    @NotNull(message = "Trọng lượng không được để trống.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Trọng lượng phải lớn hơn 0.")
    private BigDecimal weight;

    @NotNull(message = "Chiều cao không được để trống.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Chiều cao phải lớn hơn 0.")
    private BigDecimal height;

    @NotNull(message = "Chiều dài không được để trống.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Chiều dài phải lớn hơn 0.")
    private BigDecimal length;

    @NotNull(message = "Chiều rộng không được để trống.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Chiều rộng phải lớn hơn 0.")
    private BigDecimal width;

    private String status;

    @Size(max = 500, message = "URL image phải ít hơn 500 ký tự")
    private String image;

    private List<String> attributeValueID;
}
