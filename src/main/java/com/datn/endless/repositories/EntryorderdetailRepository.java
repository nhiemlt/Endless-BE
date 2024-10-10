package com.datn.endless.repositories;

import com.datn.endless.entities.Entryorderdetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EntryorderdetailRepository extends JpaRepository<Entryorderdetail, String> {
    @Query("SELECT SUM(pod.quantity) FROM Entryorderdetail pod WHERE pod.productVersionID.productVersionID = :productVersionID")
    Integer findTotalPurchasedQuantityByProductVersion(@Param("productVersionID") String productVersionID);
}