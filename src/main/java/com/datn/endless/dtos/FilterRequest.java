package com.datn.endless.dtos;

import java.math.BigDecimal;
import java.util.List;

public class FilterRequest {
    private List<String> categoryIDs;  // Sử dụng ID thay vì tên
    private List<String> brandIDs;     // Sử dụng ID thay vì tên
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    // Getter và setter
    public List<String> getCategoryIDs() {
        return categoryIDs;
    }

    public void setCategoryIDs(List<String> categoryIDs) {
        this.categoryIDs = categoryIDs;
    }

    public List<String> getBrandIDs() {
        return brandIDs;
    }

    public void setBrandIDs(List<String> brandIDs) {
        this.brandIDs = brandIDs;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }
}

