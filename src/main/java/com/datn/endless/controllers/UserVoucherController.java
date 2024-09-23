package com.datn.endless.controllers;

import com.datn.endless.dtos.VoucherDTO;
import com.datn.endless.entities.Uservoucher;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Voucher;
import com.datn.endless.repositories.UservoucherRepository;
import com.datn.endless.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-vouchers")
@CrossOrigin(origins = "*")
public class UserVoucherController {

    @Autowired
    private UservoucherRepository userVoucherRepository;

    @Autowired
    private UserRepository userRepository;


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

        // Xác thực người dùng
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User không tồn tại.");
        }

        // Tìm tất cả các voucher của người dùng
        List<Uservoucher> userVouchers = userVoucherRepository.findByUserID(user);
        if (userVouchers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Không tìm thấy voucher nào.");
        }

        // Tạo danh sách kết quả với VoucherDTO
        List<VoucherDTO> result = userVouchers.stream()
                .map(uv -> {
                    VoucherDTO voucherDTO = new VoucherDTO();
                    // Giả sử bạn có một phương thức để lấy Voucher từ Uservoucher
                    Voucher voucher = uv.getVoucherID(); // Lấy Voucher từ Uservoucher
                    if (voucher != null) {
                        voucherDTO.setVoucherID(voucher.getVoucherID());
                        voucherDTO.setVoucherCode(voucher.getVoucherCode());
                        voucherDTO.setLeastBill(voucher.getLeastBill());
                        voucherDTO.setLeastDiscount(voucher.getLeastDiscount());
                        voucherDTO.setBiggestDiscount(voucher.getBiggestDiscount());
                        voucherDTO.setDiscountLevel(voucher.getDiscountLevel());
                        voucherDTO.setDiscountForm(voucher.getDiscountForm());
                        voucherDTO.setStartDate(voucher.getStartDate());
                        voucherDTO.setEndDate(voucher.getEndDate());
                    }
                    return voucherDTO;
                })
                .collect(Collectors.toList());

        // Trả về danh sách kết quả
        return ResponseEntity.ok(result);
    }

}
