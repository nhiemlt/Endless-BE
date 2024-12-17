package com.datn.endless.services;

import com.datn.endless.dtos.ProductStatisticsDTO;
import com.datn.endless.dtos.CategoryStatisticsDTO;
import com.datn.endless.dtos.UnsoldProductDTO;
import com.datn.endless.repositories.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    public List<ProductStatisticsDTO> getTop5BestSellingProducts(String startDate, String endDate) {
        List<Map<String, Object>> result = statisticsRepository.getTop5BestSellingProducts(startDate, endDate);
        return result.stream().map(row -> new ProductStatisticsDTO(
                (String) row.get("productName"),
                (String) row.get("productVersion"),
                (String) row.get("productImage"),
                ((Number) row.get("totalImport")).longValue(),
                ((Number) row.get("totalSales")).longValue(),
                (BigDecimal) row.get("totalRevenue"),
                null,
                null
        )).collect(Collectors.toList());
    }

    public List<CategoryStatisticsDTO> getRevenueByCategory(String startDate, String endDate) {
        List<Map<String, Object>> result = statisticsRepository.getRevenueByCategory(startDate, endDate);
        return result.stream().map(row -> new CategoryStatisticsDTO(
                (String) row.get("categoryName"),
                (BigDecimal) row.get("totalRevenue")
        )).collect(Collectors.toList());
    }

    public List<UnsoldProductDTO> getUnsoldProducts() {
        List<Map<String, Object>> result = statisticsRepository.getUnsoldProducts();
        return result.stream().map(row -> new UnsoldProductDTO(
                (String) row.get("productName"),
                (String) row.get("productVersion")
        )).collect(Collectors.toList());
    }

    public List<ProductStatisticsDTO> getTotalImportAndSales() {
        List<Map<String, Object>> result = statisticsRepository.getTotalImportAndSales();
        return result.stream().map(row -> new ProductStatisticsDTO(
                (String) row.get("productName"),
                (String) row.get("productVersion"),
                (String) row.get("productImage"),
                ((Number) row.get("totalImport")).longValue(),
                ((Number) row.get("totalSales")).longValue(),
                (BigDecimal) row.get("totalRevenue"),
                (BigDecimal) row.get("importPrice"),
                (BigDecimal) row.get("exportPrice")
        )).collect(Collectors.toList());
    }

    // Phương thức mới để lấy thông tin bán hàng sản phẩm
    public List<ProductStatisticsDTO> getProductSalesSummary(String startDate, String endDate) {
        List<Map<String, Object>> result = statisticsRepository.getProductSalesSummary(startDate, endDate);
        return result.stream().map(row -> new ProductStatisticsDTO(
                (String) row.get("productName"),
                (String) row.get("productVersion"),
                (String) row.get("productImage"),
                null,
                ((Number) row.get("totalSales")).longValue(),
                (BigDecimal) row.get("totalRevenue"),
                null,
                (BigDecimal) row.get("exportPrice")
        )).collect(Collectors.toList());
    }
}
