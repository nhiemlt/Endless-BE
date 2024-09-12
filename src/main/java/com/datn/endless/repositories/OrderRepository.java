package com.datn.endless.repositories;

import com.datn.endless.entities.Order;
import com.datn.endless.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserID(User user);
}