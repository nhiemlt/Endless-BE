package com.datn.endless.repositories;

import com.datn.endless.entities.Attribute;
import com.datn.endless.entities.Attributevalue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributevalueRepository extends JpaRepository<Attributevalue, String> {
    List<Attributevalue> findByAttribute(Attribute attribute);
    Page<Attributevalue> findByAttribute(Attribute attribute, Pageable pageable);
    Page<Attributevalue> findAll(Pageable pageable);
    // Kiểm tra giá trị thuộc tính đã tồn tại cho thuộc tính cụ thể
    boolean existsByValueAndAttribute(String value, Attribute attribute);
}
