package com.datn.endless.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductVersionOrderDTO {
    private String productVersionID;
    private String versionName;
    private BigDecimal price;
    private String image;
}
