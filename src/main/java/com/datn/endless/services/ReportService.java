package com.datn.endless.services;

import com.datn.endless.dtos.RevenueReportDTO;
import com.datn.endless.dtos.ProductReportDTO;
import com.datn.endless.dtos.StockReportDTO;
import com.datn.endless.entities.Order;
import com.datn.endless.entities.Orderstatus;
import com.datn.endless.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datn.endless.entities.Orderdetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderstatusRepository orderstatusRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    @Autowired
    private EntrydetailRepository entrydetailRepository;

    @Autowired
    private OrderdetailRepository orderdetailRepository;

    public RevenueReportDTO getRevenueReportByYear(int year) {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        if (year > currentYear) {
            throw new IllegalArgumentException("Year cannot be in the future");
        }

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Order> deliveredOrders = orderRepository.findAll().stream()
                .filter(order -> {
                    Orderstatus latestStatus = orderstatusRepository.findAll().stream()
                            .filter(orderStatus -> orderStatus.getOrder().getOrderID().equals(order.getOrderID()))
                            .max(Comparator.comparing(Orderstatus::getTime))
                            .orElse(null);

                    return latestStatus != null &&
                            "Đã giao hàng".equals(latestStatus.getStatusType().getName()) &&
                            !order.getOrderDate().toLocalDate().isBefore(startDate) &&
                            !order.getOrderDate().toLocalDate().isAfter(endDate);
                })
                .collect(Collectors.toList());

        // Tính tổng doanh thu
        BigDecimal totalRevenue = deliveredOrders.stream()
                .map(Order::getTotalMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = deliveredOrders.size();
        int totalProductsSold = deliveredOrders.stream()
                .flatMap(order -> order.getOrderdetails().stream())
                .mapToInt(Orderdetail::getQuantity)
                .sum();

        List<RevenueReportDTO.Detail> details = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            LocalDate currentMonthStart = LocalDate.of(year, month, 1);
            LocalDate currentMonthEnd = currentMonthStart.withDayOfMonth(currentMonthStart.lengthOfMonth());

            BigDecimal monthlyRevenue = deliveredOrders.stream()
                    .filter(order -> {
                        LocalDate orderDate = order.getOrderDate().toLocalDate();
                        return !orderDate.isBefore(currentMonthStart) && !orderDate.isAfter(currentMonthEnd);
                    })
                    .map(Order::getTotalMoney)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            details.add(new RevenueReportDTO.Detail(currentMonthStart, monthlyRevenue));
        }

        return new RevenueReportDTO(totalRevenue, startDate, endDate, totalOrders, totalProductsSold, details);
    }

    // Thống kê xuất nhập
    public List<StockReportDTO> getStockReport(String period) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();

        // Xác định khoảng thời gian cần thống kê
        switch (period) {
            case "day":
                startDate = endDate;
                break;
            case "week":
                startDate = endDate.minusDays(7);
                break;
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "quarter":
                startDate = endDate.minusMonths(3);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
        }

        final LocalDate finalStartDate = startDate;
        final LocalDate finalEndDate = endDate;

        // Bản đồ lưu tổng số lượng
        Map<String, Long> totalOrderQuantityMap = new HashMap<>();
        Map<String, Long> totalEntryQuantityMap = new HashMap<>();

        // Tổng số lượng bán ra
        orderdetailRepository.findAll().forEach(orderDetail -> {
            Order order = orderDetail.getOrderID();
            LocalDate orderDate = order.getOrderDate().toLocalDate();
            if (!orderDate.isBefore(finalStartDate) && !orderDate.isAfter(finalEndDate)) {
                String productName = orderDetail.getProductVersionID().getVersionName();
                totalOrderQuantityMap.put(productName,
                        totalOrderQuantityMap.getOrDefault(productName, 0L) + orderDetail.getQuantity());
            }
        });

        // Tổng số lượng nhập kho
        entrydetailRepository.findAll().forEach(entryDetail -> {
            LocalDate entryDate = entryDetail.getEntry().getEntryDate().toLocalDate();
            if (!entryDate.isBefore(finalStartDate) && !entryDate.isAfter(finalEndDate)) {
                String productName = entryDetail.getProductVersionID().getVersionName();
                totalEntryQuantityMap.put(productName,
                        totalEntryQuantityMap.getOrDefault(productName, 0L) + entryDetail.getQuantity());
            }
        });

        // Tạo danh sách báo cáo
        List<StockReportDTO> stockReports = new ArrayList<>();
        productversionRepository.findAll().forEach(version -> {
            String name = version.getProductID().getName() + " - " + version.getVersionName();
            String productName = version.getVersionName();
            stockReports.add(new StockReportDTO(
                    name,
                    totalEntryQuantityMap.getOrDefault(productName, 0L),
                    totalOrderQuantityMap.getOrDefault(productName, 0L)
            ));
        });

        return stockReports;
    }

    // Thống kê sản phẩm (sản phẩm bán chạy nhất)
    public List<ProductReportDTO> getProductReport(String period) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now();

        // Xác định khoảng thời gian cần thống kê
        switch (period) {
            case "day":
                startDate = endDate;
                break;
            case "last7days":
                startDate = endDate.minusDays(6);
                break;
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "quarter":
                startDate = endDate.minusMonths(3);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
        }

        final LocalDate finalStartDate = startDate;
        final LocalDate finalEndDate = endDate;

        Map<String, Long> productCounts = new HashMap<>();

        // Tính toán số lượng bán được trong khoảng thời gian
        for (Orderdetail orderDetail : orderdetailRepository.findAll()) {
            Order order = orderDetail.getOrderID();
            LocalDate orderDate = order.getOrderDate().toLocalDate();
            if (!orderDate.isBefore(finalStartDate) && !orderDate.isAfter(finalEndDate)) {
                String productVersionName = orderDetail.getProductVersionID().getVersionName();
                productCounts.put(productVersionName,
                        productCounts.getOrDefault(productVersionName, 0L) + orderDetail.getQuantity());
            }
        }

        // Chuyển đổi thành danh sách DTO và lấy 3 sản phẩm bán chạy nhất
        return productCounts.entrySet().stream()
                .map(entry -> new ProductReportDTO(entry.getKey(), entry.getValue()))
                .sorted((p1, p2) -> p2.getTotalQuantitySold().compareTo(p1.getTotalQuantitySold()))
                .limit(3)
                .collect(Collectors.toList());
    }
}
