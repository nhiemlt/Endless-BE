package com.datn.endless.repositories;

import com.datn.endless.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    // Tìm sản phẩm theo tên chính xác
    Optional<Product> findByName(String name);

    // Tìm danh sách sản phẩm theo tên
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name%")
    List<Product> findByNameContaining(@Param("name") String name);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Thêm phương thức tìm kiếm theo CategoryID hoặc BrandID
    Page<Product> findByCategoryIDOrBrandID(String categoryId, String brandId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.categoryID.name LIKE %:name%")
    List<Product> findByCategoryNameContaining(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE p.brandID.name LIKE %:name%")
    List<Product> findByBrandNameContaining(@Param("name") String name);

    // Phương thức tổng hợp tìm kiếm theo keyword
    @Query("""
           SELECT p FROM Product p 
           WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
              OR LOWER(p.categoryID.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
              OR LOWER(p.brandID.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


    // Đếm số lượng sản phẩm
    @Query("SELECT COUNT(p) FROM Product p")
    long countProducts();

    // Đếm số lượng brand
    @Query("SELECT COUNT(DISTINCT p.brandID) FROM Product p")
    long countBrands();
}

