package com.datn.endless.controllers;

import com.datn.endless.dtos.ReportDTO;
import com.datn.endless.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        List<ReportDTO> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{reportID}")
    public ResponseEntity<ReportDTO> getReportById(@PathVariable String reportID) {
        ReportDTO report = reportService.getReportById(reportID);
        return report != null ? ResponseEntity.ok(report) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportDTO reportDTO) {
        ReportDTO createdReport = reportService.saveReport(reportDTO);
        return ResponseEntity.status(201).body(createdReport);
    }

    @PutMapping("/{reportID}")
    public ResponseEntity<ReportDTO> updateReport(@PathVariable String reportID, @RequestBody ReportDTO reportDTO) {
        ReportDTO existingReport = reportService.getReportById(reportID);
        if (existingReport != null) {
            reportDTO.setReportID(reportID);
            ReportDTO updatedReport = reportService.saveReport(reportDTO);
            return ResponseEntity.ok(updatedReport);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{reportID}")
    public ResponseEntity<Void> deleteReport(@PathVariable String reportID) {
        reportService.deleteReport(reportID);
        return ResponseEntity.noContent().build();
    }
}
