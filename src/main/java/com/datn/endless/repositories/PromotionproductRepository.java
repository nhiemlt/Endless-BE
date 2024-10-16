package com.datn.endless.repositories;

import com.datn.endless.entities.Promotionproduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromotionproductRepository extends JpaRepository<Promotionproduct, String> {
    @Query("SELECT pp FROM Promotionproduct pp WHERE pp.productVersionID.productVersionID = :productVersionID")
    List<Promotionproduct> findByProductVersionID(@Param("productVersionID") String productVersionID);


}