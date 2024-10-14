package com.datn.endless.services;

import com.datn.endless.dtos.RevenueReportDTO;
import com.datn.endless.dtos.ProductReportDTO;
import com.datn.endless.dtos.StockReportDTO;
import com.datn.endless.entities.Orderdetail;
import com.datn.endless.entities.Productversion;
import com.datn.endless.repositories.OrderdetailRepository;
import com.datn.endless.repositories.ProductversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderdetailRepository orderdetailRepository;
    @Autowired
    private ProductversionRepository productversionRepository;

    // Thống kê kho hàng
    public List<StockReportDTO> getStockReport() {
        List<StockReportDTO> stockReports = new ArrayList<>();
        List<Productversion> allVersions = productversionRepository.findAll();

        // Bản đồ để giữ tổng số lượng đã đặt hàng cho mỗi phiên bản
        Map<String, Long> totalOrderQuantityMap = new HashMap<>();

        // Tính tổng số lượng đã đặt hàng
        for (Orderdetail orderDetail : orderdetailRepository.findAll()) {
            String productVersionId = orderDetail.getProductVersionID().getProductVersionID();
            totalOrderQuantityMap.put(productVersionId,
                    totalOrderQuantityMap.getOrDefault(productVersionId, 0L) + orderDetail.getQuantity());
        }

        // Tạo báo cáo cho từng phiên bản sản phẩm
        for (Productversion version : allVersions) {
            StockReportDTO stockReport = new StockReportDTO();
            stockReport.setVersionName(version.getVersionName());
            // Cập nhật đúng thuộc tính cho tổng số lượng nhập
            stockReport.setTotalEntryQuantity(version.getPurchasePrice().longValue());
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

    // Thống kê doanh thu
    public RevenueReportDTO getRevenueReport() {
        BigDecimal totalRevenue = orderdetailRepository.findAll().stream()
                .map(orderDetail -> orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RevenueReportDTO(totalRevenue);
    }
}
