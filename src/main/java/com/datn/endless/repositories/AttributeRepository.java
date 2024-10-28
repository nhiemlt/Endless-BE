package com.datn.endless.repositories;

import com.datn.endless.entities.Attribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute, String> {
    // Kiểm tra thuộc tính đã tồn tại bằng tên
    boolean existsByAttributeName(String attributeName);
    Page<Attribute> findByAttributeNameContainingIgnoreCase(String name, Pageable pageable);
}
