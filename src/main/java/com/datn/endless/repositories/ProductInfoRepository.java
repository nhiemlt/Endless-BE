package com.datn.endless.repositories;

import com.datn.endless.entities.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductInfoRepository extends JpaRepository<ProductInfo, String> {

    @Query("SELECT p " +
            "FROM ProductInfo p " +
            "JOIN p.productversions pv " +
            "Where (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.categoryID.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.brandID.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(pv.versionName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryIDs IS NULL OR p.categoryID.categoryID IN :categoryIDs) " +
            "AND (:brandIDs IS NULL OR p.brandID.brandID IN :brandIDs) " +
            "AND (:minPrice IS NULL OR pv.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR pv.price <= :maxPrice)")
    List<ProductInfo> findProductInfoByCriteria(
            @Param("keyword") String keyword,
            @Param("categoryIDs") List<String> categoryIDs,
            @Param("brandIDs") List<String> brandIDs,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice);
}
