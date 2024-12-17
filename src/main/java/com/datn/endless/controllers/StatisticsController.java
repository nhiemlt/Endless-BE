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

    @GetMapping("/total-import-sales")
    public ResponseEntity<?> getTotalImportAndSales(@RequestParam String startDate, @RequestParam String endDate) {
        List<ProductStatisticsDTO> result = statisticsService.getTotalImportAndSales(startDate, endDate);
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
}
