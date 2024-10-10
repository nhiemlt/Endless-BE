package com.datn.endless.repositories;

import com.datn.endless.entities.Entryorder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;


public interface EntryorderRepository extends JpaRepository<Entryorder, String> {
    @Query("SELECT p FROM Entryorder p WHERE (:startDate IS NULL OR p.purchaseDate >= :startDate) AND (:endDate IS NULL OR p.purchaseDate <= :endDate)")
    Page<Entryorder> findByPurchaseDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    Page<Entryorder> findAll(Pageable pageable);
}