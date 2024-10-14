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

    // Thống kê kho hàng
    @GetMapping("/stock")
    public ResponseEntity<List<StockReportDTO>> getStockReport() {
        List<StockReportDTO> stockReports = reportService.getStockReport();
        return ResponseEntity.ok(stockReports);
    }

    // Thống kê sản phẩm
    @GetMapping("/products")
    public ResponseEntity<List<ProductReportDTO>> getProductReport() {
        List<ProductReportDTO> productReports = reportService.getProductReport();
        return ResponseEntity.ok(productReports);
    }

    // Thống kê doanh thu
    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportDTO> getRevenueReport() {
        RevenueReportDTO revenueReport = reportService.getRevenueReport();
        return ResponseEntity.ok(revenueReport);
    }
}
