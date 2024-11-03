package com.datn.endless.repositories;

import com.datn.endless.entities.Order;
import com.datn.endless.entities.Orderdetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderdetailRepository extends JpaRepository<Orderdetail, String> {
    List<Orderdetail> findByOrderID(Order order);

    // Tính tổng số lượng sản phẩm đã bán
    @Query("SELECT SUM(od.quantity) FROM Orderdetail od WHERE od.productVersionID.productVersionID = :productVersionID")
    Integer findTotalSoldQuantityByProductVersion(@Param("productVersionID") String productVersionID);

    @Query("SELECT od.productVersionID.productVersionID, SUM(od.quantity) as totalQuantity " +
            "FROM Orderdetail od " +
            "WHERE od.orderID.orderDate >= :startOfMonth AND od.orderID.orderDate <= :endOfMonth " +
            "GROUP BY od.productVersionID.productVersionID " +
            "ORDER BY totalQuantity DESC")
    Page<Object[]> findTopSellingProductVersionsInMonth(@Param("startOfMonth") LocalDateTime startOfMonth,
                                                        @Param("endOfMonth") LocalDateTime endOfMonth,
                                                        Pageable pageable);

    @Query("SELECT od.productVersionID.productVersionID, SUM(od.quantity) as totalQuantity " +
            "FROM Orderdetail od " +
            "GROUP BY od.productVersionID.productVersionID " +
            "ORDER BY totalQuantity DESC")
    Page<Object[]> findTopSellingProductVersionsAllTime(Pageable pageable);

}