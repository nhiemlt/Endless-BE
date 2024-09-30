package com.datn.endless.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DTOProductVersionUser {

    private String productId; // ID duy nhất của sản phẩm
    private String productName; // Tên sản phẩm bằng tiếng Việt
    private String productNameEnglish; // Tên sản phẩm bằng tiếng Anh
    private String description; // Mô tả chi tiết về sản phẩm
    private String category; // Danh mục mà sản phẩm thuộc về
    private String brand; // Thương hiệu của sản phẩm
    private BigDecimal originalPrice; // Giá gốc của sản phẩm
    private BigDecimal discountPrice; // Giá sau khi giảm giá
    private double discountPercentage; // Tỷ lệ phần trăm giảm giá
    private String image; // Hinhf ảnh chi tiết sản phẩm

    private List<ProductVersionDTO> productVersions; // Danh sách các phiên bản của sản phẩm
    private List<AttributeDTO> attributes; // Danh sách các thuộc tính kỹ thuật của sản phẩm
    private double averageRating; // Đánh giá trung bình của sản phẩm
    private int numberOfReviews; // Số lượng đánh giá mà sản phẩm nhận được
    private int quantityAvailable; // Số lượng sản phẩm hiện có trong kho
    private List<PromotionDTO> promotions; // Danh sách các chương trình khuyến mãi áp dụng cho sản phẩm

}
