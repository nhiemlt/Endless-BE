package com.datn.endless.repositories;

import com.datn.endless.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {


    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

