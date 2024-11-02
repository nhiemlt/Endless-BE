package com.datn.endless.controllers;

import com.datn.endless.dtos.PromotionDetailDTO;
import com.datn.endless.models.PromotionDetailModel;
import com.datn.endless.services.PromotionDetailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promotion-details")
public class PromotionDetailController {

    @Autowired
    private PromotionDetailService promotionDetailService;

    // Tạo mới một PromotionDetail
    @PostMapping
    public ResponseEntity<PromotionDetailDTO> createPromotionDetail(@Valid @RequestBody PromotionDetailModel promotionDetailModel) {
        PromotionDetailDTO createdDetail = promotionDetailService.createPromotionDetail(promotionDetailModel);
        return ResponseEntity.ok(createdDetail);
    }

    // Cập nhật PromotionDetail theo ID
    @PutMapping("/{id}")
    public ResponseEntity<PromotionDetailDTO> updatePromotionDetail( @PathVariable String id,@Valid @RequestBody PromotionDetailModel promotionDetailModel) {
        PromotionDetailDTO updatedDetail = promotionDetailService.updatePromotionDetail(id, promotionDetailModel);
        return ResponseEntity.ok(updatedDetail);
    }

    // Cập nhật phương thức lấy tất cả PromotionDetails với phân trang
    @GetMapping
    public ResponseEntity<Page<PromotionDetailDTO>> getAllPromotionDetails(Pageable pageable) {
        Page<PromotionDetailDTO> details = promotionDetailService.getAllPromotionDetails(pageable);
        return ResponseEntity.ok(details);
    }

    // Lấy PromotionDetail theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PromotionDetailDTO> getPromotionDetailById(@PathVariable String id) {
        Optional<PromotionDetailDTO> detail = promotionDetailService.getPromotionDetailById(id);
        return detail.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Xóa PromotionDetail theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePromotionDetail(@PathVariable String id) {
        promotionDetailService.deletePromotionDetail(id);
        return ResponseEntity.ok("Xóa thành công PromotionDetail với ID: " + id);
    }


    // Xử lý lỗi xác thực
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
