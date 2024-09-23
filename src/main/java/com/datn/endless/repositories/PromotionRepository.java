package com.datn.endless.repositories;

import com.datn.endless.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {
    boolean existsByName(String name);
    boolean existsByNameAndPromotionIDNot(String name, String promotionID);
    List<Promotion> findByNameContainingIgnoreCase(String name);
}
