package com.datn.endless.repositories;

import com.datn.endless.entities.Order;
import com.datn.endless.entities.Orderdetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderdetailRepository extends JpaRepository<Orderdetail, String> {
    List<Orderdetail> findByOrderID(Order order);

    // Tính tổng số lượng sản phẩm đã bán
    @Query("SELECT SUM(od.quantity) FROM Orderdetail od WHERE od.productVersionID.productVersionID = :productVersionID")
    Integer findTotalSoldQuantityByProductVersion(@Param("productVersionID") String productVersionID);

}