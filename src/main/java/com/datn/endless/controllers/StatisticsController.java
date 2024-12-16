package com.datn.endless.controllers;

import com.datn.endless.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/getStatistics")
    public ResponseEntity<Map<String, Object>> getStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Map<String, Object> statistics = statisticsService.getStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }
}
