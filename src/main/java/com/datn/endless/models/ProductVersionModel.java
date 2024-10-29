package com.datn.endless.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVersionModel {

    @NotNull(message = "Mã sản phẩm không được để trống.")
    private String productID;

    @Size(max = 255, message = "Tên phiên bản không được vượt quá 255 ký tự.")
    @NotNull(message = "Tên phiên bản không được để trống.")
    private String versionName;

    @NotNull(message = "Giá nhập không được để trống.")
    private BigDecimal purchasePrice;

    @NotNull(message = "Giá bán không được để trống.")
    private BigDecimal price;

    @NotNull(message = "Trọng lượng không được để trống.")
    private BigDecimal weight;

    @NotNull(message = "Chiều cao không được để trống.")
    private BigDecimal height;

    @NotNull(message = "Chiều dài không được để trống.")
    private BigDecimal length;

    @NotNull(message = "Chiều rộng không được để trống.")
    private BigDecimal width;

    @NotNull(message = "Hình ảnh không được để trống.")
    private String image;

    @NotNull(message = "Vui lòng chọn các thuộc tính.")
    private List<String> attributeValueID;
}
