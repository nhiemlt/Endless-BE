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
    // Tìm đánh giá theo userId với phân trang
    Page<Rating> findByUserID_UserID(String userId, Pageable pageable);

    @Query("SELECT r FROM Rating r WHERE (:productName IS NULL OR r.orderDetailID.productVersionID.versionName LIKE %:productName%)")
    Page<Rating> findByProductNameOrAll(@Param("productName") String productName, Pageable pageable);

    // Lấy danh sách đánh giá theo productVersionID
    List<Rating> findByOrderDetailID_ProductVersionID_ProductVersionID(String productVersionID);

    // Phương thức để tìm điểm trung bình rating theo productVersionID
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.orderDetailID.productVersionID.productVersionID = :productVersionID")
    Optional<Double> findAverageRatingByProductVersionId(@Param("productVersionID") String productVersionID);

    // Đếm số lượng đánh giá theo productVersionID
    long countByOrderDetailID_ProductVersionID_ProductVersionID(String productVersionID);
}
