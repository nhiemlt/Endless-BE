package com.datn.endless.services;

import com.datn.endless.dtos.ReportDTO;
import com.datn.endless.entities.Report;
import com.datn.endless.repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReportDTO getReportById(String reportID) {
        Optional<Report> report = reportRepository.findById(reportID);
        return report.map(this::toDTO).orElse(null);
    }

    public ReportDTO saveReport(ReportDTO dto) {
        Report report = toEntity(dto);
        Report savedReport = reportRepository.save(report);
        return toDTO(savedReport);
    }

    public void deleteReport(String reportID) {
        if (reportRepository.existsById(reportID)) {
            reportRepository.deleteById(reportID);
        }
    }

    private ReportDTO toDTO(Report report) {
        return ReportDTO.builder()
                .reportID(report.getReportID())
                .title(report.getTitle())
                .description(report.getDescription())
                .creationDate(report.getCreationDate())
                .createdBy(report.getCreatedBy())
                .isActive(report.getIsActive())
                .build();
    }

    private Report toEntity(ReportDTO dto) {
        return Report.builder()
                .reportID(dto.getReportID())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .creationDate(dto.getCreationDate())
                .createdBy(dto.getCreatedBy())
                .isActive(dto.getIsActive())
                .build();
    }
}
