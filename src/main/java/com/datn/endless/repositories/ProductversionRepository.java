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

    // Lọc các sản phẩm có trạng thái active
    List<Productversion> findByStatus(String status);

    @Query("SELECT pv FROM Productversion pv WHERE pv.versionName LIKE %:keyword% or pv.productID.name LIKE %:keyword% ")
    Page<Productversion> findByVersionNameContaining2(@Param("keyword") String keyword, Pageable pageable);


    List<Productversion> findByProductID(Product product);


    // Kiểm tra xem đã có versionName cho sản phẩm chưa
    boolean existsByProductIDAndVersionName(Product product, String versionName);



    // Kiểm tra trùng versionName với productID và loại trừ phiên bản hiện tại (để không kiểm tra trùng chính nó)
    @Query("SELECT CASE WHEN COUNT(pv) > 0 THEN true ELSE false END " +
            "FROM Productversion pv WHERE pv.productID = :product AND pv.versionName = :versionName AND pv.productVersionID <> :productVersionID")
    boolean existsByProductIDAndVersionNameAndNotId(@Param("product") Product product,
                                                    @Param("versionName") String versionName,
                                                    @Param("productVersionID") String productVersionID);


    public List<Productversion> findByProductID_BrandID_Name(String brandName);





}
