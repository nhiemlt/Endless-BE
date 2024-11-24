package com.datn.endless.repositories;

import com.datn.endless.entities.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {

    @Query("SELECT p FROM Promotion p WHERE :keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Promotion> findByNameContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(p) > 0 FROM Promotion p JOIN p.promotionproducts pp " +
            "WHERE pp.productVersionID.productVersionID = :productVersionID " +
            "AND ((p.startDate BETWEEN :startDate AND :endDate) " +
            "OR (p.endDate BETWEEN :startDate AND :endDate))")
    boolean existsByProductVersionAndTimeOverlap(@Param("productVersionID") String productVersionID,
                                                 @Param("startDate") Instant startDate,
                                                 @Param("endDate") Instant endDate);

    // Kiểm tra xem tên khuyến mãi đã tồn tại hay chưa
    @Query("SELECT COUNT(p) > 0 FROM Promotion p WHERE LOWER(p.name) = LOWER(:name)")
    boolean existsByName(@Param("name") String name);

    public boolean existsByNameAndPromotionIDNot(String name, String promotionID);

}
