package com.datn.endless.controllers;

import com.datn.endless.dtos.ErrorResponse;
import com.datn.endless.dtos.VoucherDTO;
import com.datn.endless.exceptions.VoucherNotFoundException;
import com.datn.endless.models.VoucherModel;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(origins = "*")
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

    // Phương thức xử lý ngoại lệ để trả về thông báo lỗi xác thực
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Delete Voucher
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteVoucher(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            voucherService.deleteVoucher(id);
            response.put("success", true);
            response.put("message", "Voucher deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
