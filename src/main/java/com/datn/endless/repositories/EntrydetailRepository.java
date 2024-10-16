package com.datn.endless.repositories;

import com.datn.endless.entities.Entrydetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EntrydetailRepository extends JpaRepository<Entrydetail, String> {
    @Query("SELECT SUM(pod.quantity) FROM Entrydetail pod WHERE pod.productVersionID.productVersionID = :productVersionID")
    Integer findTotalPurchasedQuantityByProductVersion(@Param("productVersionID") String productVersionID);
}