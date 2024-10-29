package com.datn.endless.repositories;

import com.datn.endless.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    // Tìm sản phẩm theo tên chính xác
    Optional<Product> findByName(String name);

    // Tìm tất cả sản phẩm
    @Query("SELECT p FROM Product p")
    Page<Product> findAllProducts(Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Thêm phương thức tìm kiếm theo CategoryID hoặc BrandID
    Page<Product> findByCategoryIDOrBrandID(String categoryId, String brandId, Pageable pageable);
}

