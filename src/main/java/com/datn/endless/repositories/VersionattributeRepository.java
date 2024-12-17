package com.datn.endless.repositories;

import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Versionattribute;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VersionattributeRepository extends JpaRepository<Versionattribute, String> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Versionattribute va WHERE va.productVersionID.productVersionID = :productVersionID")
    void deleteByProductVersionID(@Param("productVersionID") String productVersionID);

//    List<Versionattribute> findByProductVersionID(String productVersionID);

    // Nếu cần so sánh với ID, chắc chắn ID là kiểu String
    public abstract List<Versionattribute> findByProductVersionID(Productversion productVersion);


    @Query("SELECT va FROM Versionattribute va WHERE va.productVersionID.productVersionID = :productVersionID")
    List<Versionattribute> findByProductVersionID(@Param("productVersionID") String productVersionID);

    @Query("SELECT DISTINCT va.attributeValue.attributeValueID FROM Versionattribute va WHERE va.productVersionID.productID.productID = :productID")
    List<String> findByProductID(@Param("productID") String productID);

}