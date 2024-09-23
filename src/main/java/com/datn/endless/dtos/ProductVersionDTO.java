package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductVersionDTO {
    private String productVersionID;
    private ProductForProcVersionDTO product;
    private String versionName;
    private BigDecimal purchasePrice;
    private BigDecimal price;
    private String status;
    private String image;
    private BigDecimal discountPrice;
    List<VersionAttributeDTO> versionAttributes;
}
