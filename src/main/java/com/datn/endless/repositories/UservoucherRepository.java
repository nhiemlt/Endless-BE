package com.datn.endless.repositories;

import com.datn.endless.entities.User;
import com.datn.endless.entities.Uservoucher;
import com.datn.endless.entities.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface UservoucherRepository extends JpaRepository<Uservoucher, String> {

    // Tìm UserVoucher dựa trên User và Voucher
    Uservoucher findByUserIDAndVoucherID(User userID, Voucher voucherID);
    // Tìm tất cả UserVoucher của một User
    List<Uservoucher> findByUserID(User userID);

    @Query("SELECT v FROM Voucher v JOIN Uservoucher uv ON uv.voucherID = v WHERE uv.userID.username = :username")
    List<Voucher> findByUsername(@Param("username") String username);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO uservouchers (user_id, voucher_id) " +
            "SELECT u.user_id, :voucherID FROM users u WHERE u.active = true", nativeQuery = true)
    void addVoucherToAllActiveUsers(@Param("voucherID") String voucherID);

    @Query("SELECT v FROM Voucher v WHERE v.startDate <= :now AND v.endDate >= :now AND :totalAmount >= v.leastBill")
    List<Voucher> findValidVouchersByAmount(@Param("now") LocalDateTime now, @Param("totalAmount") BigDecimal totalAmount);

}