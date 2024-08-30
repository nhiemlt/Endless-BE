package com.datn.endless.repositories;

import com.datn.endless.entities.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepository extends JpaRepository<Voucher, String> {
}