package com.datn.endless.repositories;

import com.datn.endless.entities.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {
    boolean existsByName(String name);
    boolean existsByNameAndPromotionIDNot(String name, String promotionID);
    List<Promotion> findByNameContainingIgnoreCase(String name);
    Page<Promotion> findByNameContaining(String name, Pageable pageable);


    @Query("SELECT p FROM Promotion p WHERE "
            + "(:name IS NULL OR p.name LIKE %:name%) AND "
            + "(:startDate IS NULL OR p.startDate >= :startDate) AND "
            + "(:endDate IS NULL OR p.endDate <= :endDate)")
    Page<Promotion> findByCriteria(
            @Param("name") String name,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
