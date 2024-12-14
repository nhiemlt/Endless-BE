package com.datn.endless.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoDTO {
    private String productID;
    private String name;
    private BigDecimal price;
    private String CategoryName;
    private String BrandName;

    private double discountPercentage;
    private  double quantityAvailable;
    private  double   quantitySold;

    private BigDecimal discountPrice;
    private double averageRating;
    private long numberOfReviews;

    private List<ProductDetailDTO> productVersionDTOs;
}
