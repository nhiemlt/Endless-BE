package com.datn.endless.controllers;

import com.datn.endless.dtos.PromotionDTO;
import com.datn.endless.exceptions.ProductVersionNotFoundException;
import com.datn.endless.exceptions.PromotionAlreadyExistsException;
import com.datn.endless.exceptions.PromotionNotFoundException;
import com.datn.endless.models.PromotionModel;
import com.datn.endless.services.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public ResponseEntity<?> getAllPromotions(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortBy", defaultValue = "createDate") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        try {
            // Chuyển đổi hướng sắp xếp
            Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            // Gọi service với các tham số phân trang và tìm kiếm
            Page<PromotionDTO> promotions = promotionService.getAllPromotions(keyword, pageable);

            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @GetMapping("/{promotionID}")
    public ResponseEntity<?> getPromotionById(@PathVariable String promotionID) {
        try {
            PromotionDTO promotionDTO = promotionService.getPromotionById(promotionID);
            if (promotionDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy khuyến mãi với ID: " + promotionID);
            }
            return ResponseEntity.ok(promotionDTO);
        } catch (PromotionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }



    @PostMapping()
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotionModel promotionModel, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Dữ liệu đầu vào không hợp lệ: ");
            result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append("; "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        try {
            PromotionDTO savedPromotion = promotionService.createPromotion(promotionModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPromotion);
        } catch (ProductVersionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (PromotionAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }


    @PutMapping("/{promotionID}")
    public ResponseEntity<?> updatePromotion(@PathVariable String promotionID, @Valid @RequestBody PromotionModel promotionModel, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Dữ liệu đầu vào không hợp lệ: ");
            result.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append("; "));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        try {
            PromotionDTO updatedPromotion = promotionService.updatePromotion(promotionID, promotionModel);
            return ResponseEntity.ok(updatedPromotion);
        } catch (PromotionNotFoundException | ProductVersionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (PromotionAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @RequestMapping("/{promotionID}/toggle")
    public ResponseEntity<?> toggleActive(@PathVariable String promotionID) {
        try {
            PromotionDTO updatedPromotion = promotionService.toggleActive(promotionID);
            return ResponseEntity.ok(updatedPromotion);
        } catch (PromotionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @DeleteMapping("/{promotionID}")
    public ResponseEntity<?> deletePromotion(@PathVariable String promotionID) {
        try {
            promotionService.deletePromotion(promotionID);
            return ResponseEntity.ok("Khuyến mãi đã được xóa thành công.");
        } catch (PromotionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}