package com.datn.endless.repositories;

import com.datn.endless.entities.Promotiondetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromotiondetailRepository extends JpaRepository<Promotiondetail, String> {
}