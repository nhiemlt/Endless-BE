package com.datn.endless.controllers;

import com.datn.endless.entities.Uservoucher;
import com.datn.endless.entities.User;
import com.datn.endless.entities.Voucher;
import com.datn.endless.repositories.UservoucherRepository;
import com.datn.endless.repositories.UserRepository;
import com.datn.endless.repositories.VoucherRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/uservouchers")
@CrossOrigin(origins = "*")
public class UserVoucherController {

    @Autowired
    HttpSession session;

    @Autowired
    UservoucherRepository userVoucherRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VoucherRepository voucherRepository;

    @Autowired
    HttpServletRequest req;

    @GetMapping
    public ResponseEntity<List<Uservoucher>> getUserVouchers() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        List<Uservoucher> userVouchers = userVoucherRepository.findByUserID(user);
        return ResponseEntity.ok(userVouchers);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToUserVouchers(@RequestBody Uservoucher userVoucher) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Voucher voucher = voucherRepository.findById(userVoucher.getVoucherID().getVoucherID()).orElse(null);
        if (voucher == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher not found");
        }

        Uservoucher existingUserVoucher = userVoucherRepository.findByUserIDAndVoucherID(user, voucher);
        if (existingUserVoucher != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Voucher already assigned to user");
        } else {
            userVoucher.setUserID(user);
            userVoucher.setVoucherID(voucher);
            userVoucherRepository.save(userVoucher);
        }

        return ResponseEntity.ok("Voucher added to user: " + username);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUserVoucher(@PathVariable String id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Tìm UserVoucher theo ID
        Uservoucher userVoucher = userVoucherRepository.findById(id).orElse(null);
        if (userVoucher == null || !userVoucher.getUserID().equals(user)) {
            return ResponseEntity.notFound().build();
        }

        // Xóa UserVoucher khỏi danh sách của người dùng
        userVoucherRepository.delete(userVoucher);
        return ResponseEntity.noContent().build();
    }
}
