package com.datn.endless.repositories;

import com.datn.endless.entities.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeRepository extends JpaRepository<Attribute, String> {
}