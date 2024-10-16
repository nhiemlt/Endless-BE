package com.datn.endless.controllers;

import com.datn.endless.dtos.PromotionDTO;
import com.datn.endless.models.PromotionModel;
import com.datn.endless.services.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    // Tạo mới một promotion
    @PostMapping
    public ResponseEntity<PromotionDTO> createPromotion(
            @RequestParam String name,
            @RequestParam String enName,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) MultipartFile poster,
            @RequestParam String enDescription) {

        PromotionModel promotionModel = new PromotionModel();
        promotionModel.setName(name);
        promotionModel.setEnName(enName);
        promotionModel.setStartDate(startDate);
        promotionModel.setEndDate(endDate);
        promotionModel.setPoster(poster);
        promotionModel.setEnDescription(enDescription);

        PromotionDTO createdPromotion = promotionService.createPromotion(promotionModel);
        return ResponseEntity.ok(createdPromotion);
    }

    // Cập nhật promotion theo ID
    @PutMapping("/{id}")
    public ResponseEntity<PromotionDTO> updatePromotion(
            @PathVariable String id,
            @RequestParam String name,
            @RequestParam String enName,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) MultipartFile poster,
            @RequestParam String enDescription) {

        PromotionModel promotionModel = new PromotionModel();
        promotionModel.setName(name);
        promotionModel.setEnName(enName);
        promotionModel.setStartDate(startDate);
        promotionModel.setEndDate(endDate);
        promotionModel.setPoster(poster);
        promotionModel.setEnDescription(enDescription);

        PromotionDTO updatedPromotion = promotionService.updatePromotion(id, promotionModel);
        return ResponseEntity.ok(updatedPromotion);
    }

    // Lấy tất cả các promotion
    @GetMapping
    public ResponseEntity<List<PromotionDTO>> getAllPromotions() {
        List<PromotionDTO> promotions = promotionService.getAllPromotions();
        return ResponseEntity.ok(promotions);
    }

    // Lấy promotion theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable String id) {
        Optional<PromotionDTO> promotion = promotionService.getPromotionById(id);
        return promotion.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Xóa promotion theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable String id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    // Lọc promotion theo tiêu chí
    @GetMapping("/search")
    public ResponseEntity<Page<PromotionDTO>> searchPromotions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Pageable pageable) {

        Page<PromotionDTO> promotions = promotionService.findPromotionsByCriteria(name, startDate, endDate, pageable);
        return ResponseEntity.ok(promotions);
    }
}
