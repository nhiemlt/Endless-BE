package com.datn.endless.services;

import com.datn.endless.repositories.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    public Map<String, Object> getStatistics(String startDate, String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        List<Object[]> statisticsData = statisticsRepository.callGetStatistics(start, end);

        return processStatisticsData(statisticsData);
    }

    private Map<String, Object> processStatisticsData(List<Object[]> statisticsData) {
        Map<String, Object> result = new HashMap<>();

        if (statisticsData != null) {
            for (Object[] row : statisticsData) {
                try {
                    String category = (String) row[0];
                    Double totalRevenue = (Double) row[1];
                    Double percentage = (Double) row[2];
                    result.put(category, Map.of("totalRevenue", totalRevenue, "percentage", percentage));
                } catch (Exception e) {
                    e.printStackTrace(); // Log the error for debugging
                }
            }
        } else {
            System.out.println("No statistics data found.");
        }

        return result;
    }

}
