package com.datn.endless.repositories;

import com.datn.endless.entities.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, String> {

    Optional<Voucher> findById(String id);
    Optional<Voucher> findByVoucherCode(String voucherCode);
}