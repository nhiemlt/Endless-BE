package com.datn.endless.repositories;

import com.datn.endless.entities.District;
import com.datn.endless.entities.Province;
import com.datn.endless.entities.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, String> {
    @Query("SELECT p FROM Province p where p.code =:code")
    Optional<Province> findProvinceByProvinceCode(@Param("code") String provinceCode);
}