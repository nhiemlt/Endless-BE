package com.datn.endless.controllers;

import com.datn.endless.entities.Uservoucher;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Voucher;
import com.datn.endless.repositories.UservoucherRepository;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-vouchers")
@CrossOrigin(origins = "*")
public class UserVoucherController {

    @Autowired
    private UservoucherRepository userVoucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    // Lấy tất cả voucher của người dùng
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

        // Trả về danh sách Uservoucher
        return ResponseEntity.ok(userVouchers);
    }


    // Áp dụng voucher cho người dùng
    @PostMapping("/apply/{voucherID}")
    public ResponseEntity<String> applyVoucher(@PathVariable String voucherID, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated. Please log in.");
        }

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }

        Optional<Voucher> optionalVoucher = voucherRepository.findById(voucherID);
        if (optionalVoucher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher not found.");
        }

        Voucher voucher = optionalVoucher.get();
        Uservoucher userVoucher = userVoucherRepository.findByUserIDAndVoucherID(user, voucher);

        if (userVoucher != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Voucher already applied.");
        }

        Uservoucher newUserVoucher = new Uservoucher();
        newUserVoucher.setUserID(user);
        newUserVoucher.setVoucherID(voucher);
        newUserVoucher.setStatus("APPLIED"); // Set status accordingly

        userVoucherRepository.save(newUserVoucher);
        return ResponseEntity.status(HttpStatus.CREATED).body("Voucher applied successfully.");
    }

    // Hủy áp dụng voucher
    @DeleteMapping("/remove/{voucherID}")
    public ResponseEntity<String> removeVoucher(@PathVariable String voucherID, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated. Please log in.");
        }

        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
        }

        Optional<Voucher> optionalVoucher = voucherRepository.findById(voucherID);
        if (optionalVoucher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher not found.");
        }

        Voucher voucher = optionalVoucher.get();
        Uservoucher userVoucher = userVoucherRepository.findByUserIDAndVoucherID(user, voucher);

        if (userVoucher == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher not applied.");
        }

        userVoucherRepository.delete(userVoucher);
        return ResponseEntity.ok("Voucher removed successfully.");
    }
}
