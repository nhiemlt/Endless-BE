package com.datn.endless.controllers;

import com.datn.endless.dtos.MonthlyRevenueDTO;
import com.datn.endless.services.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/revenue")
public class RevenueController {

    @Autowired
    private RevenueService revenueService;

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getRevenue(@RequestParam String startDate,
                                                 @RequestParam String endDate) {
        try {
            BigDecimal revenue = revenueService.getTotalRevenue(startDate, endDate);
            return ResponseEntity.ok(revenue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/monthly")
    public ResponseEntity<?> getMonthlyRevenue(@RequestParam int year) {
        try {
            List<MonthlyRevenueDTO> revenueList = revenueService.getMonthlyRevenue(year);
            return ResponseEntity.ok(revenueList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Năm không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi trên hệ thống.");
        }
    }
}
