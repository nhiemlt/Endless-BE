package com.datn.endless.repositories;

import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Promotiondetail;
import com.datn.endless.entities.Promotionproduct;
import com.datn.endless.entities.Versionattribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromotionproductRepository extends JpaRepository<Promotionproduct, String> {
    @Query("SELECT pp FROM Promotionproduct pp WHERE pp.productVersionID.productVersionID = :productVersionID")
    List<Promotionproduct> findByProductVersionID(@Param("productVersionID") String productVersionID);

    public abstract List<Promotionproduct> findByPromotionDetailID(Promotiondetail promotiondetail);

    // Phương thức để tìm kiếm theo percentDiscount trong PromotionDetail
    @Query("SELECT pp FROM Promotionproduct pp JOIN pp.promotionDetailID pd WHERE pd.percentDiscount = :percentDiscount")
    Page<Promotionproduct> findByPromotionDetailPercentDiscount(Pageable pageable, @Param("percentDiscount") Double percentDiscount);

}