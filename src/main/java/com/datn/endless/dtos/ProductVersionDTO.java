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
    private BigDecimal weight; // là weight
    private BigDecimal height;
    private BigDecimal length;
    private BigDecimal width;
    private String status;
    private String image;

    private double discountPercentage;
    private  double quantityAvailable;
    private  double   quantitySold;

    private BigDecimal discountPrice;
    private double averageRating;
    private long    NumberOfReviews;


    List<VersionAttributeDTO> versionAttributes;
    private List<PromotionDTO> promotions; // Danh sách các chương trình khuyến mãi áp dụng cho sản phẩm



}
