package com.datn.endless.repositories;

import com.datn.endless.entities.Purchaseorder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseorderRepository extends JpaRepository<Purchaseorder, String> {
}