package com.datn.endless.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class StatisticsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getTop5BestSellingProducts(String startDate, String endDate) {
        String sql = "CALL getTop5BestSellingProducts(?, ?)";
        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    public List<Map<String, Object>> getRevenueByCategory(String startDate, String endDate) {
        String sql = "CALL getRevenueByCategory(?, ?)";
        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    public List<Map<String, Object>> getUnsoldProducts() {
        String sql = "CALL getUnsoldProducts()";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getTotalImportAndSales() {
        String sql = "CALL getTotalImportAndSales()";
        return jdbcTemplate.queryForList(sql);
    }

    // Phương thức mới để gọi stored procedure getProductSalesSummary
    public List<Map<String, Object>> getProductSalesSummary(String startDate, String endDate)
        { String sql = "CALL getProductSalesSummary(?, ?)";
        return jdbcTemplate.queryForList(sql, startDate, endDate);}
}
