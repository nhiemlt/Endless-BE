package com.datn.endless.repositories;

import com.datn.endless.entities.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, String> {
    Page<Rating> findByUserID_UserID(String userId, Pageable pageable);
    List<Rating> findByOrderDetailID_ProductVersionID_ProductVersionID(String productVersionID);

}