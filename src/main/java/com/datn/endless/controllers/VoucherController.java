package com.datn.endless.controllers;

import com.datn.endless.entities.Voucher;
import com.datn.endless.repositories.VoucherRepository;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    // Thêm Voucher
    @PostMapping("/add")
    public ResponseEntity<String> addVoucher(@RequestBody Voucher voucher) {
        String validationError = validateVoucher(voucher);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
        }
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
    public ResponseEntity<String> updateVoucher(@PathVariable String id, @RequestBody Voucher updatedVoucher) {
        String validationError = validateVoucher(updatedVoucher);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
        }
        // Kiểm tra ID
        System.out.println("Updating voucher with ID: " + id);

        Optional<Voucher> voucherOptional = voucherRepository.findById(id);

        // Kiểm tra nếu voucher không tồn tại
        if (voucherOptional.isEmpty()) {
            System.out.println("Voucher with ID: " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Voucher not found");
        }

        Voucher voucher = voucherOptional.get();

        // Cập nhật các thuộc tính của voucher, ngoại trừ voucherCode
        voucher.setLeastBill(updatedVoucher.getLeastBill());
        voucher.setLeastDiscount(updatedVoucher.getLeastDiscount());
        voucher.setBiggestDiscount(updatedVoucher.getBiggestDiscount());
        voucher.setDiscountLevel(updatedVoucher.getDiscountLevel());
        voucher.setDiscountForm(updatedVoucher.getDiscountForm());
        voucher.setStartDate(updatedVoucher.getStartDate());
        voucher.setEndDate(updatedVoucher.getEndDate());

        // Lưu voucher đã cập nhật
        voucherRepository.save(voucher);

        // Trả về thông báo đã cập nhật
        return ResponseEntity.ok("Voucher updated successfully");
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

    private String validateVoucher(Voucher voucher) {
        if (voucher.getVoucherCode() == null || voucher.getVoucherCode().trim().isEmpty()) {
            return "VoucherCode must not be empty";
        }
        if (voucher.getLeastBill() == null || voucher.getLeastBill().compareTo(BigDecimal.ZERO) <= 0) {
            return "LeastBill must be a positive number";
        }
        if (voucher.getLeastDiscount() == null || voucher.getLeastDiscount().compareTo(BigDecimal.ZERO) <= 0) {
            return "LeastDiscount must be a positive number";
        }
        if (voucher.getBiggestDiscount() == null || voucher.getBiggestDiscount().compareTo(BigDecimal.ZERO) <= 0) {
            return "BiggestDiscount must be a positive number";
        }
        if (voucher.getDiscountLevel() == null || voucher.getDiscountLevel() <= 0) {
            return "DiscountLevel must be a positive number";
        }
        if (voucher.getDiscountForm() == null || voucher.getDiscountForm().trim().isEmpty()) {
            return "DiscountForm must not be empty";
        }
        if (voucher.getStartDate() == null) {
            return "StartDate must not be null";
        }
        if (voucher.getEndDate() == null) {
            return "EndDate must not be null";
        }
        if (voucher.getStartDate().isAfter(voucher.getEndDate())) {
            return "StartDate must be before EndDate";
        }
        return null; // Valid
    }

}
