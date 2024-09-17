package com.datn.endless.repositories;

import com.datn.endless.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT o FROM Order o WHERE " +
            "(:userID IS NULL OR o.userID.userID LIKE %:userID%) AND " +
            "(:orderAddress IS NULL OR o.orderAddress LIKE %:orderAddress%) AND " +
            "(:orderPhone IS NULL OR o.orderPhone LIKE %:orderPhone%) AND " +
            "(:orderName IS NULL OR o.orderName LIKE %:orderName%)")
    Page<Order> findAllByUserIDContainingAndOrderAddressContainingAndOrderPhoneContainingAndOrderNameContaining(
            @Param("userID") String userID,
            @Param("orderAddress") String orderAddress,
            @Param("orderPhone") String orderPhone,
            @Param("orderName") String orderName,
            Pageable pageable);
}