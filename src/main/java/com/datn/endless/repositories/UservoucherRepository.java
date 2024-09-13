package com.datn.endless.repositories;

import com.datn.endless.entities.User;
import com.datn.endless.entities.Uservoucher;
import com.datn.endless.entities.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UservoucherRepository extends JpaRepository<Uservoucher, String> {

    // Tìm UserVoucher dựa trên User và Voucher
    Uservoucher findByUserIDAndVoucherID(User userID, Voucher voucherID);
    // Tìm tất cả UserVoucher của một User
    List<Uservoucher> findByUserID(User userID);
}