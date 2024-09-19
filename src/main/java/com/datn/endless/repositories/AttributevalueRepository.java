package com.datn.endless.repositories;

import com.datn.endless.entities.Attribute;
import com.datn.endless.entities.Attributevalue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributevalueRepository extends JpaRepository<Attributevalue, String> {
    boolean existsByValue(String value);

    List<Attributevalue> findByValue(String value);




}
