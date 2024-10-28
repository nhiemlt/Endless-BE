package com.datn.endless.services;

import com.datn.endless.dtos.RevenueReportDTO;
import com.datn.endless.dtos.ProductReportDTO;
import com.datn.endless.dtos.StockReportDTO;
import com.datn.endless.entities.Order;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Entrydetail;
import com.datn.endless.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datn.endless.entities.Orderdetail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    @Autowired
    private EntrydetailRepository entrydetailRepository;

    @Autowired
    private OrderdetailRepository orderdetailRepository;

    // Thống kê doanh thu với ngày bắt đầu và kết thúc
    public RevenueReportDTO getRevenueReport(LocalDate startDate, LocalDate endDate) {
        // Nếu không có ngày bắt đầu và ngày kết thúc, sử dụng ngày hiện tại
        if (startDate == null || endDate == null) {
            startDate = LocalDate.now();
            endDate = LocalDate.now();
        }

        final LocalDate finalStartDate = startDate;
        final LocalDate finalEndDate = endDate;

        // Lấy tổng doanh thu từ bảng Order
        LocalDateTime startDateTime = finalStartDate.atStartOfDay();
        LocalDateTime endDateTime = finalEndDate.atTime(LocalTime.MAX);

        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(order -> {
                    LocalDateTime orderDate = order.getOrderDate();
                    return (orderDate.isEqual(startDateTime) || orderDate.isEqual(endDateTime) ||
                            (orderDate.isAfter(startDateTime) && orderDate.isBefore(endDateTime)));
                })
                .map(Order::getTotalMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        return new RevenueReportDTO(totalRevenue, finalStartDate, finalEndDate);
    }

    // Thống kê kho hàng
    public List<StockReportDTO> getStockReport() {
        List<StockReportDTO> stockReports = new ArrayList<>();
        List<Productversion> allVersions = productversionRepository.findAll();

        // Tạo một bản đồ để giữ tổng số lượng đã đặt hàng cho mỗi phiên bản
        Map<String, Long> totalOrderQuantityMap = new HashMap<>();

        // Tính toán tổng số lượng đã đặt hàng
        for (Orderdetail orderDetail : orderdetailRepository.findAll()) {
            String productVersionId = orderDetail.getProductVersionID().getProductVersionID();
            totalOrderQuantityMap.put(productVersionId,
                    totalOrderQuantityMap.getOrDefault(productVersionId, 0L) + orderDetail.getQuantity());
        }

        // Tính toán tổng số lượng đã nhập cho từng phiên bản
        Map<String, Long> totalEntryQuantityMap = new HashMap<>();
        for (Entrydetail entryDetail : entrydetailRepository.findAll()) {
            String productVersionId = entryDetail.getProductVersionID().getProductVersionID();
            totalEntryQuantityMap.put(productVersionId,
                    totalEntryQuantityMap.getOrDefault(productVersionId, 0L) + entryDetail.getQuantity());
        }

        // Tạo báo cáo cho từng phiên bản sản phẩm
        for (Productversion version : allVersions) {
            StockReportDTO stockReport = new StockReportDTO();
            stockReport.setVersionName(version.getVersionName());
            stockReport.setTotalEntryQuantity(totalEntryQuantityMap.getOrDefault(version.getProductVersionID(), 0L));
            stockReport.setTotalOrderQuantity(totalOrderQuantityMap.getOrDefault(version.getProductVersionID(), 0L));
            stockReports.add(stockReport);
        }

        return stockReports;
    }

    // Thống kê sản phẩm (sản phẩm bán chạy nhất)
    public List<ProductReportDTO> getProductReport() {
        Map<String, Long> productCounts = new HashMap<>();

        for (Orderdetail orderDetail : orderdetailRepository.findAll()) {
            String productVersionName = orderDetail.getProductVersionID().getVersionName();
            productCounts.put(productVersionName,
                    productCounts.getOrDefault(productVersionName, 0L) + orderDetail.getQuantity());
        }

        // Chuyển đổi sang danh sách DTO và lấy 3 sản phẩm bán chạy nhất
        return productCounts.entrySet().stream()
                .map(entry -> new ProductReportDTO(entry.getKey(), entry.getValue()))
                .sorted((p1, p2) -> p2.getTotalQuantitySold().compareTo(p1.getTotalQuantitySold()))
                .limit(3)
                .collect(Collectors.toList());
    }
}
