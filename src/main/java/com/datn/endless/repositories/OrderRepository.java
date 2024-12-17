package com.datn.endless.repositories;

import com.datn.endless.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT o FROM Order o WHERE " +
            "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
            "(:endDate IS NULL OR o.orderDate <= :endDate) AND (" +
            ":keywords IS NULL OR :keywords = '' OR " +
            "o.userID.userID LIKE CONCAT('%', :keywords, '%') OR " +
            "o.orderAddress LIKE CONCAT('%', :keywords, '%') OR " +
            "o.orderPhone LIKE CONCAT('%', :keywords, '%') OR " +
            "o.orderName LIKE CONCAT('%', :keywords, '%')" +
            ") " +
            "ORDER BY o.orderDate DESC")
    Page<Order> findAllByFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("keywords") String keywords,
            Pageable pageable);

    List<Order> findByUserID_Username(String username);

    List<Order> findByUserID_UsernameOrderByOrderDateDesc(String username);
}