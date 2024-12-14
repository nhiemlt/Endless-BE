package com.datn.endless.dtos;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class ProductDetailDTO {
    private String productVersionID;
    private String versionName;
    private BigDecimal price;
    private String status;
    private String image;

    private double discountPercentage;
    private double quantityAvailable;
    private double quantitySold;

    private BigDecimal discountPrice;
    private double averageRating;
    private long NumberOfReviews;

    List<VersionAttributeInfoDTO> versionAttributes;
}
