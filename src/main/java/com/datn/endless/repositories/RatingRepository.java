package com.datn.endless.repositories;

import com.datn.endless.entities.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, String> {
    Page<Rating> findByUserID_UserID(String userId, Pageable pageable);
    List<Rating> findByOrderDetailID_ProductVersionID_ProductVersionID(String productVersionID);

    // Phương thức để tìm điểm trung bình rating theo productVersionID
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.orderDetailID.productVersionID.productVersionID = :productVersionID")
    Optional<Double> findAverageRatingByProductVersionId(@Param("productVersionID") String productVersionID);

}