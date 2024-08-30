package com.datn.endless.repositories;

import com.datn.endless.entities.Orderdetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderdetailRepository extends JpaRepository<Orderdetail, String> {
}