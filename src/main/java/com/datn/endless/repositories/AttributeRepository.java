package com.datn.endless.repositories;

import com.datn.endless.entities.Attribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttributeRepository extends JpaRepository<Attribute, String> {

    Page<Attribute> findByAttributeNameContainingIgnoreCase(String name, Pageable pageable);
}
