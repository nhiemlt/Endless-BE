package com.datn.endless.repositories;

import com.datn.endless.entities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, String> {

    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) = LOWER(:name)")
    Optional<Brand> findByNameIgnoreCase(@Param("name") String name);


    List<Brand> findByNameContainingIgnoreCase(String name);  // Tìm kiếm theo tên (bỏ qua phân biệt hoa thường)

    @Query("SELECT b FROM Brand b WHERE b.name LIKE %?1%")
    Page<Brand> searchByName(String name, Pageable pageable);

}
