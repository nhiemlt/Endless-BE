package com.datn.endless.repositories;

import com.datn.endless.entities.Purchaseorderdetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseorderdetailRepository extends JpaRepository<Purchaseorderdetail, String> {
    @Query("SELECT SUM(pod.quantity) FROM Purchaseorderdetail pod WHERE pod.productVersionID.productVersionID = :productVersionID")
    Integer findTotalPurchasedQuantityByProductVersion(@Param("productVersionID") String productVersionID);
}