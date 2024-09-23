package com.datn.endless.repositories;

import com.datn.endless.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    // Tìm kiếm danh mục theo tên
    Optional<Category> findByName(String name);
    Optional<Category> findByEnName(String enName);
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Category> findByEnNameContainingIgnoreCase(String enName, Pageable pageable);
}
