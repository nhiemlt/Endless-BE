package com.datn.endless.repositories;

import com.datn.endless.entities.Product;
import com.datn.endless.entities.Productversion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductversionRepository extends JpaRepository<Productversion, String> {

    @Query("SELECT pv FROM Productversion pv WHERE pv.versionName LIKE %:keyword% or " +
            "pv.productID.name LIKE %:keyword% or" +
            " pv.productID.categoryID.name LIKE %:keyword% or " +
            "pv.productID.brandID.name LIKE %:keyword% ")
    Page<Productversion> findByVersionNameContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT pv FROM Productversion pv WHERE pv.status = 'Active'")
    Page<Productversion> findByStatusActive(Pageable pageable);

    @Query("SELECT pv FROM Productversion pv WHERE pv.versionName LIKE %:keyword% or pv.productID.name LIKE %:keyword% ")
    Page<Productversion> findByVersionNameContaining2(@Param("keyword") String keyword, Pageable pageable);


    List<Productversion> findByProductID(Product product);
    Productversion findFirstByProductID(Product product);







}
