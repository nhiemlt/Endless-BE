package com.datn.endless.repositories;

import com.datn.endless.entities.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WardRepository extends JpaRepository<Ward, String> {
}