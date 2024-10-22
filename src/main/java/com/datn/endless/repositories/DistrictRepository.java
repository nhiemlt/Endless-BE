package com.datn.endless.repositories;

import com.datn.endless.entities.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, String> {
    @Query("SELECT d FROM District d where d.code =:code")
    Optional<District> findDistrictByDistrictCode(@Param("code") String districtCode);

    @Query("SELECT d FROM District d where d.provinceCode.code =:code")
    List<District> findDistrictByProvinceCode(@Param("code") String province);
}