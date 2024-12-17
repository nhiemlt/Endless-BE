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
  
      @Query("SELECT pv, SUM(od.quantity) as totalQuantity " +
            "FROM Orderdetail od JOIN od.productVersionID pv " +
            "WHERE pv.productID.categoryID.categoryID = :categoryID " +
            "GROUP BY pv " +
            "ORDER BY totalQuantity DESC")
    Page<Object[]> findTopSellingProductVersionsByCategory(@Param("categoryID") String categoryID, Pageable pageable);

    @Query("SELECT pv, SUM(od.quantity) as totalQuantity " +
            "FROM Orderdetail od JOIN od.productVersionID pv " +
            "WHERE pv.productID.brandID.brandID = :brandID " +
            "GROUP BY pv " +
            "ORDER BY totalQuantity DESC")
    Page<Object[]> findTopSellingProductVersionsByBrand(@Param("brandID") String brandID, Pageable pageable);

    @Query("SELECT SUM(od.quantity) " +
            "FROM Orderdetail od " +
            "JOIN od.orderID o " +
            "JOIN o.orderdetails odetail " +
            "JOIN Orderstatus os ON os.order = o " +
            "JOIN os.statusType st " +
            "WHERE st.id = 7 " +
            "AND od.productVersionID.productVersionID = :productVersionID")
    Integer countCancelledProductVersionQuantity(@Param("productVersionID") String productVersionID);
}