package com.datn.endless.services;

import com.datn.endless.dtos.MonthlyRevenueDTO;
import com.datn.endless.repositories.RevenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RevenueService {

    @Autowired
    private RevenueRepository revenueRepository;

    public BigDecimal getTotalRevenue(String startDateStr, String endDateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDateTime startDateTime = LocalDate.parse(startDateStr, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDateStr, formatter).plusDays(1).atStartOfDay();

        return revenueRepository.calculateTotalRevenue(startDateTime, endDateTime)
                .orElse(BigDecimal.ZERO);
    }

    public List<MonthlyRevenueDTO> getMonthlyRevenue(int year) {
        if (year > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Năm không thể là năm tương lai.");
        }

        LocalDateTime startOfYear = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime endOfYear = LocalDate.of(year + 1, 1, 1).atStartOfDay();

        List<Object[]> result = revenueRepository.calculateRevenueByMonth(startOfYear, endOfYear);

        List<MonthlyRevenueDTO> monthlyRevenueList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            BigDecimal revenue = BigDecimal.ZERO;
            for (Object[] row : result) {
                int month = ((Number) row[0]).intValue();
                if (month == i) {
                    revenue = (BigDecimal) row[1];
                    break;
                }
            }
            monthlyRevenueList.add(new MonthlyRevenueDTO(i, revenue));
        }

        return monthlyRevenueList;
    }
}
