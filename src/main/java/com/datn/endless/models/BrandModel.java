package com.datn.endless.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BrandModel {
    @NotBlank(message = "Tên thương hiệu không được để trống") // Kiểm tra chuỗi không trống và khác null
    @Size(max = 255, message = "Tên thương hiệu phải ít hơn 255 ký tự")
    private String name;

    @Size(max = 500, message = "URL logo phải ít hơn 500 ký tự")
    private String logo; // URL logo (nếu dùng URL để lưu logo)
}
