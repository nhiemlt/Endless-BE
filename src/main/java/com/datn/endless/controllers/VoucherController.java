package com.datn.endless.controllers;

import com.datn.endless.dtos.ErrorResponse;
import com.datn.endless.dtos.VoucherDTO;
import com.datn.endless.exceptions.VoucherNotFoundException;
import com.datn.endless.models.VoucherModel;
import com.datn.endless.models.VoucherModel2;
import com.datn.endless.services.VoucherService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllVouchers(
            @RequestParam(required = false) String voucherCode,
            @RequestParam(required = false) BigDecimal leastBill,
            @RequestParam(required = false) BigDecimal leastDiscount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "voucherID") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        Map<String, Object> response = new HashMap<>();
        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<VoucherDTO> vouchers = voucherService.getAllVouchers(voucherCode, leastBill, leastDiscount, pageable);

            response.put("success", true);
            response.put("data", vouchers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getVoucherById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            VoucherDTO voucher = voucherService.getVoucherById(id); // Gọi phương thức từ service

            response.put("success", true);
            response.put("message", "Voucher found successfully"); // Trả về thông báo thành côn    g
            response.put("data", voucher); // Include the found voucher in the response
            return ResponseEntity.ok(response);
        } catch (VoucherNotFoundException e) {
            response.put("success", false);
            response.put("message", "Voucher not found"); // Trả về thông báo không tìm thấy voucher
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // API để thêm voucher cho tất cả các user có trạng thái active là true
    @PostMapping("/add-to-all-active-users")
    public ResponseEntity<String> addVoucherToAllActiveUsers(@RequestBody VoucherModel voucherModel) {
        try {
            voucherService.addVoucherAllUser(voucherModel);
            return ResponseEntity.ok("Voucher created and assigned to all active users successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request"+e.getMessage());
        }
    }

    @PostMapping("/add-voucher-users")
    public ResponseEntity<String> addVoucherUsers(@RequestBody VoucherModel2 voucherModel) {
        try {
            voucherService.addVoucherForUser(voucherModel);
            return ResponseEntity.ok("Voucher created and assigned to users successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request"+e.getMessage());
        }
    }


    @PostMapping("/add")
    public ResponseEntity<Object> addVoucher(@Valid @RequestBody VoucherModel voucherModel) {
        Map<String, Object> response = new HashMap<>();
        try {
            voucherService.addVoucher(voucherModel);
            response.put("success", true);
            response.put("message", "Voucher added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (VoucherNotFoundException e) { // Thay bằng loại ngoại lệ thực tế
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Voucher already exists", e.getMessage()));
        } catch (ValidationException e) { // Thêm loại ngoại lệ khác nếu cần
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", e.getMessage()));
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateVoucher(@PathVariable String id, @Valid @RequestBody VoucherModel updatedVoucher) {
        Map<String, Object> response = new HashMap<>();
        try {
            voucherService.updateVoucher(id, updatedVoucher);
            response.put("success", true);
            response.put("message", "Voucher updated successfully");
            return ResponseEntity.ok(response);
        } catch (VoucherNotFoundException e) { // Thay bằng loại ngoại lệ thực tế
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Voucher not found", e.getMessage()));
        } catch (ValidationException e) { // Một loại ngoại lệ khác nếu có
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Validation error", e.getMessage()));
        } catch (Exception e) {
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", e.getMessage()));
        }
    }


}
