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

    @Query("SELECT pv FROM Productversion pv JOIN pv.productID p WHERE p.name LIKE %:name%")
    List<Productversion> findByProductNameContaining(@Param("name") String name);

    @Query("SELECT pv FROM Productversion pv WHERE pv.versionName LIKE %:keyword% or pv.productID.name LIKE %:keyword% ")
    Page<Productversion> findByVersionNameContaining2(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT pv FROM Productversion pv WHERE pv.versionName LIKE %:versionName% OR pv.productID.name LIKE %:versionName%")
    Page<Productversion> findByVersionNameContaining(@Param("versionName") String versionName, Pageable pageable);


    List<Productversion> findByProductID(Product product);

    @Query("SELECT pv FROM Productversion pv WHERE pv.status = 'Active'")
    Page<Productversion> findByStatusActive(Pageable pageable);



}
