package com.datn.endless.controllers;

import com.datn.endless.dtos.RevenueReportDTO;
import com.datn.endless.dtos.ProductReportDTO;
import com.datn.endless.dtos.StockReportDTO;
import com.datn.endless.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // Thống kê kho hàng theo khoảng thời gian
    @GetMapping("/stock")
    public ResponseEntity<List<StockReportDTO>> getStockReport(@RequestParam(required = false, defaultValue = "month") String period) {
        List<StockReportDTO> stockReports = reportService.getStockReport(period);
        return ResponseEntity.ok(stockReports);
    }

    // Thống kê sản phẩm theo khoảng thời gian
    @GetMapping("/products")
    public ResponseEntity<List<ProductReportDTO>> getProductReport(@RequestParam(required = false, defaultValue = "month") String period) {
        List<ProductReportDTO> productReports = reportService.getProductReport(period);
        return ResponseEntity.ok(productReports);
    }

    // Thống kê doanh thu theo năm
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueReport(@RequestParam("year") int year) {
        try {
            RevenueReportDTO revenueReport = reportService.getRevenueReportByYear(year);
            return ResponseEntity.ok(revenueReport);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}