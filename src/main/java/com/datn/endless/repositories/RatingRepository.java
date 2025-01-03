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

    @Query("SELECT r FROM Rating r WHERE (:keyword IS NULL OR " +
            "r.orderDetailID.productVersionID.versionName LIKE %:keyword% OR " +
            "r.userID.username LIKE %:keyword% OR " +
            "r.userID.fullname LIKE %:keyword% OR " +
            "r.comment LIKE %:keyword%) " +
            "AND (:value = 0 OR r.ratingValue = :value) " +
            "AND (:month = 0 OR FUNCTION('MONTH', r.ratingDate) = :month) " +
            "AND (:year = 0 OR FUNCTION('YEAR', r.ratingDate) = :year)")
    Page<Rating> findByKeyWord(
            @Param("keyword") String keyword,
            @Param("value") int value,
            @Param("month") int month,
            @Param("year") int year,
            Pageable pageable
    );

    // Lấy danh sách đánh giá theo productVersionID
    List<Rating> findByOrderDetailID_ProductVersionID_ProductVersionID(String productVersionID);

    // Phương thức để tìm điểm trung bình rating theo productVersionID
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.orderDetailID.productVersionID.productVersionID = :productVersionID")
    Optional<Double> findAverageRatingByProductVersionId(@Param("productVersionID") String productVersionID);

    // Đếm số lượng đánh giá theo productVersionID
    long countByOrderDetailID_ProductVersionID_ProductVersionID(String productVersionID);

    boolean existsByOrderDetailID_orderDetailID(String orderDetailID);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.ratingValue BETWEEN 1 AND 5")
    Long countTotalRatings();

    @Query("SELECT r.ratingValue, COUNT(r.ratingValue) FROM Rating r WHERE r.ratingValue BETWEEN 1 AND 5 GROUP BY r.ratingValue")
    List<Object[]> findRatingsGroupedByValue();
}
