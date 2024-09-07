package com.datn.endless.controllers;

import com.datn.endless.entities.Voucher;
import com.datn.endless.repositories.VoucherRepository;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(origins = "*")
public class VoucherController {

    @Autowired
    private VoucherRepository voucherRepository;

    @GetMapping
    @PermitAll
    public List<Voucher> getAllVouchers() {
        List<Voucher> vouchers = voucherRepository.findAll();
        return vouchers;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addVoucher(@RequestBody Voucher voucher) {
        // Kiểm tra xem voucher code đã tồn tại hay chưa
        Optional<Voucher> existingVoucher = voucherRepository.findByVoucherCode(voucher.getVoucherCode());
        if (existingVoucher.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Voucher code already exists");
        }

        voucherRepository.save(voucher);
        return ResponseEntity.status(HttpStatus.CREATED).body("Voucher added successfully");
    }

    // Sửa Voucher
    @PutMapping("/update/{id}")
    public ResponseEntity<Voucher> updateVoucher(@PathVariable String id, @RequestBody Voucher updatedVoucher) {
        Optional<Voucher> voucherOptional = voucherRepository.findById(id);
        if (voucherOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Voucher voucher = voucherOptional.get();
        voucher.setVoucherCode(updatedVoucher.getVoucherCode());
        voucher.setLeastBill(updatedVoucher.getLeastBill());
        voucher.setLeastDiscount(updatedVoucher.getLeastDiscount());
        voucher.setBiggestDiscount(updatedVoucher.getBiggestDiscount());
        voucher.setDiscountLevel(updatedVoucher.getDiscountLevel());
        voucher.setDiscountForm(updatedVoucher.getDiscountForm());
        voucher.setStartDate(updatedVoucher.getStartDate());
        voucher.setEndDate(updatedVoucher.getEndDate());

        voucherRepository.save(voucher);
        return ResponseEntity.ok(voucher);
    }

    // Xóa Voucher
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteVoucher(@PathVariable String id) {
        Optional<Voucher> voucherOptional = voucherRepository.findById(id);
        if (voucherOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher not found");
        }

        voucherRepository.deleteById(id);
        return ResponseEntity.ok("Voucher deleted successfully");
    }
}
