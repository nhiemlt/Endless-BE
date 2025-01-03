package com.datn.endless.controllers;

import com.datn.endless.dtos.VoucherDTO;
import com.datn.endless.repositories.UservoucherRepository;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.services.UserVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user-vouchers")
public class UserVoucherController {

    @Autowired
    private UservoucherRepository userVoucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  UserVoucherService userVoucherService;


    @GetMapping
    public ResponseEntity<?> getUserVouchers() {
        // Lấy thông tin người dùng từ SecurityContext
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Kiểm tra kiểu của principal
        if (!(principal instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User chưa đăng nhập.");
        }

        UserDetails userDetails = (UserDetails) principal;
        String username = userDetails.getUsername();

        // Gọi service để lấy danh sách voucher còn hạn sử dụng
        List<VoucherDTO> result = userVoucherService.getValidUserVouchers();
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Không có voucher còn hạn sử dụng.");
        }

        // Trả về danh sách kết quả
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-amount")
    public ResponseEntity<?> getVoucherByAmountMoney(@RequestParam BigDecimal totalAmount) {
        // Lấy danh sách voucher hợp lệ
        List<VoucherDTO> validVouchers = userVoucherService.getVouchersByAmount(totalAmount);

        // Kiểm tra kết quả
        if (validVouchers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Không có voucher phù hợp.");
        }

        return ResponseEntity.ok(validVouchers);
    }

}
