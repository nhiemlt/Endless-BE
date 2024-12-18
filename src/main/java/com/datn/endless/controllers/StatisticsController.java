package com.datn.endless.controllers;

import com.datn.endless.dtos.ProductStatisticsDTO;
import com.datn.endless.dtos.CategoryStatisticsDTO;
import com.datn.endless.dtos.UnsoldProductDTO;
import com.datn.endless.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/top5bestsale")
    public ResponseEntity<?> getTop5BestSellingProducts(@RequestParam String startDate, @RequestParam String endDate) {
        List<ProductStatisticsDTO> result = statisticsService.getTop5BestSellingProducts(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/revenue-by-category")
    public ResponseEntity<List<CategoryStatisticsDTO>> getRevenueByCategory(@RequestParam String startDate, @RequestParam String endDate) {
        List<CategoryStatisticsDTO> result = statisticsService.getRevenueByCategory(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/unsold-products")
    public ResponseEntity<List<UnsoldProductDTO>> getUnsoldProducts() {
        List<UnsoldProductDTO> result = statisticsService.getUnsoldProducts();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/total-import-sales")
    public ResponseEntity<List<ProductStatisticsDTO>> getTotalImportAndSales() {
        List<ProductStatisticsDTO> result = statisticsService.getTotalImportAndSales();
        return ResponseEntity.ok(result);
    }

    // Endpoint mới để lấy thông tin bán hàng sản phẩm
    @GetMapping("/product-sales-summary")
    public ResponseEntity<List<ProductStatisticsDTO>> getProductSalesSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<ProductStatisticsDTO> result = statisticsService.getProductSalesSummary(startDate, endDate);
        return ResponseEntity.ok(result);
    }
}

