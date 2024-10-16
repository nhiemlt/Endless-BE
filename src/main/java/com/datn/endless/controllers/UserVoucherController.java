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

import java.util.List;

@RestController
@RequestMapping("/api/user-vouchers")
@CrossOrigin(origins = "*")
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
        List<VoucherDTO> result = userVoucherService.getValidUserVouchers(username);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Không có voucher còn hạn sử dụng.");
        }

        // Trả về danh sách kết quả
        return ResponseEntity.ok(result);
    }
}
