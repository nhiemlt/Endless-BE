package com.datn.endless.repositories;

import com.datn.endless.entities.Purchaseorder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PurchaseorderRepository extends JpaRepository<Purchaseorder, String> {
    List<Purchaseorder> findByPurchaseDateBetween(LocalDate startDate, LocalDate endDate);
}