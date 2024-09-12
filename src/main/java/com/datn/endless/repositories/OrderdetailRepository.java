package com.datn.endless.repositories;

import com.datn.endless.entities.Order;
import com.datn.endless.entities.Orderdetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderdetailRepository extends JpaRepository<Orderdetail, String> {
    List<Orderdetail> findByOrderID(Order order);
}