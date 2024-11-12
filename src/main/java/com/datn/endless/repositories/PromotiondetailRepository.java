package com.datn.endless.repositories;

import com.datn.endless.entities.Promotion;
import com.datn.endless.entities.Promotiondetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PromotiondetailRepository extends JpaRepository<Promotiondetail, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Promotiondetail pd WHERE pd.promotionID = :promotion")
    void deleteByPromotionID(@Param("promotion") Promotion promotion);
}