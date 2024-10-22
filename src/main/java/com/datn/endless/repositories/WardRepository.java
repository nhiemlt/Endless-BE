package com.datn.endless.repositories;

import com.datn.endless.entities.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WardRepository extends JpaRepository<Ward, String> {
    List<Ward> findByDistrictCode_Code(String districtCode);

    @Query("SELECT w FROM Ward w where w.code =:code")
    Optional<Ward>  findWardByWardCode(@Param("code") String wardCode);
}