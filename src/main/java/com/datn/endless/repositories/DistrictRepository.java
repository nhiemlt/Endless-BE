package com.datn.endless.repositories;

import com.datn.endless.entities.District;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<District, String> {
}