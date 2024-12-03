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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getVoucherById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        try {
            VoucherDTO voucher = voucherService.getVoucherById(id); // Gọi phương thức từ service

            response.put("success", true);
            response.put("message", "Đã tìm thấy voucher"); // Trả về thông báo thành côn    g
            response.put("data", voucher); // Include the found voucher in the response
            return ResponseEntity.ok(response);
        } catch (VoucherNotFoundException e) {
            response.put("success", false);
            response.put("message", "Không tìm thấy voucher"); // Trả về thông báo không tìm thấy voucher
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/add-to-all-active-users")
    public ResponseEntity<Object> addVoucherToAllActiveUsers(
            @Valid @RequestBody VoucherModel voucherModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Xử lý lỗi xác thực
            Map<String, String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage()
                    ));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            voucherService.addVoucherAllUser(voucherModel);
            return ResponseEntity.ok("Thêm voucher và cấp cho tất cả người dùng đang hoạt động thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu: " + e.getMessage());
        }
    }

    @PostMapping("/add-voucher-users")
    public ResponseEntity<Object> addVoucherUsers(
            @Valid @RequestBody VoucherModel2 voucherModel, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Xử lý lỗi xác thực
            Map<String, String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage()
                    ));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            voucherService.addVoucherForUser(voucherModel);
            return ResponseEntity.ok("Thêm voucher và cấp cho người dùng thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi trong quá trình xử lý yêu cầu: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateVoucher(
            @PathVariable String id,
            @Valid @RequestBody VoucherModel updatedVoucher,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Xử lý lỗi xác thực
            Map<String, String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            fieldError -> fieldError.getField(),
                            fieldError -> fieldError.getDefaultMessage()
                    ));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        Map<String, Object> response = new HashMap<>();
        try {
            voucherService.updateVoucher(id, updatedVoucher);
            response.put("success", true);
            response.put("message", "Cập nhật voucher thành công");
            return ResponseEntity.ok(response);
        } catch (VoucherNotFoundException e) {
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Voucher không tìm thấy", e.getMessage()));
        } catch (ValidationException e) {
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Lỗi xác thực", e.getMessage()));
        } catch (Exception e) {
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Đã xảy ra lỗi không mong muốn", e.getMessage()));
        }
    }


}
