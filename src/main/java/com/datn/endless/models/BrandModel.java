package com.datn.endless.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BrandModel {
    @Size(max = 255)
    @NotNull
    private String name;

    private String logo;
}
