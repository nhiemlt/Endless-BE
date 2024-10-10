package com.datn.endless.repositories;

import com.datn.endless.entities.Purchaseorder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;


public interface PurchaseorderRepository extends JpaRepository<Purchaseorder, String> {
    @Query("SELECT p FROM Purchaseorder p WHERE (:startDate IS NULL OR p.purchaseDate >= :startDate) AND (:endDate IS NULL OR p.purchaseDate <= :endDate)")
    Page<Purchaseorder> findByPurchaseDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    Page<Purchaseorder> findAll(Pageable pageable);
}