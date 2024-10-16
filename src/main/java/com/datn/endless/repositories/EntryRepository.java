package com.datn.endless.repositories;

import com.datn.endless.entities.Entry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;


public interface EntryRepository extends JpaRepository<Entry, String> {
    @Query("SELECT e FROM Entry e WHERE (:startDate IS NULL OR e.orderDate >= :startDate) AND (:endDate IS NULL OR e.orderDate <= :endDate)")
    Page<Entry> findByPurchaseDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    Page<Entry> findAll(Pageable pageable);
}