package com.datn.endless.repositories;

import com.datn.endless.dtos.VoucherDTO;
import com.datn.endless.entities.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, String> {

    // Tìm voucher theo mã, leastBill, leastDiscount
    Optional<Voucher> findByVoucherCode(String voucherCode);

    @Query("SELECT v FROM Voucher v " +
            "WHERE (:voucherCode IS NULL OR v.voucherCode LIKE %:voucherCode%) " +
            "AND (:leastBill IS NULL OR v.leastBill = :leastBill) " +
            "AND (:leastDiscount IS NULL OR v.leastDiscount = :leastDiscount)")
    Page<Voucher> findByFilters(@Param("voucherCode") String voucherCode,
                                @Param("leastBill") BigDecimal leastBill,
                                @Param("leastDiscount") BigDecimal leastDiscount,
                                Pageable pageable);

    Optional<Voucher> findById(String id);

}