package com.datn.endless.repositories;

import com.datn.endless.entities.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeRepository extends JpaRepository<Attribute, String> {
    List<Attribute> findByAttributeNameContaining(String name);
    boolean existsByAttributeName(String name);
}
