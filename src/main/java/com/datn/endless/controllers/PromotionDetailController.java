package com.datn.endless.controllers;

import com.datn.endless.dtos.PromotionDetailDTO;
import com.datn.endless.entities.Promotion;
import com.datn.endless.entities.Promotiondetail;
import com.datn.endless.repositories.PromotionRepository;
import com.datn.endless.repositories.PromotiondetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/promotion-details")
public class PromotionDetailController {

    @Autowired
    private PromotiondetailRepository promotiondetailRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    // 1. Create PromotionDetail
    @PostMapping
    public ResponseEntity<String> createPromotionDetail(@RequestBody PromotionDetailDTO dto) {
        if (dto.getPromotionID() == null || dto.getPromotionID().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Promotion ID must not be null or empty.");
        }

        Optional<Promotion> promotion = promotionRepository.findById(dto.getPromotionID());
        if (!promotion.isPresent()) {
            return ResponseEntity.badRequest().body("Promotion with ID " + dto.getPromotionID() + " does not exist.");
        }

        if (dto.getPercentDiscount() == null || dto.getPercentDiscount() < 0) {
            return ResponseEntity.badRequest().body("Percent discount must be a non-negative integer.");
        }

        Promotiondetail promotionDetail = new Promotiondetail();
        promotionDetail.setPromotionID(promotion.get());
        promotionDetail.setPercentDiscount(dto.getPercentDiscount());

        promotiondetailRepository.save(promotionDetail);
        return ResponseEntity.status(HttpStatus.CREATED).body("Promotion detail created successfully.");
    }

    // 2. Read all PromotionDetails
    @GetMapping
    public ResponseEntity<List<Promotiondetail>> getAllPromotionDetails() {
        List<Promotiondetail> promotionDetails = promotiondetailRepository.findAll();
        return ResponseEntity.ok(promotionDetails);
    }

    // 3. Read a specific PromotionDetail by ID
    @GetMapping("/{id}")
    public ResponseEntity<Promotiondetail> getPromotionDetailById(@PathVariable String id) {
        Optional<Promotiondetail> promotionDetail = promotiondetailRepository.findById(id);
        return promotionDetail.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4. Update PromotionDetail
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePromotionDetail(@PathVariable String id, @RequestBody PromotionDetailDTO dto) {
        Optional<Promotiondetail> existingPromotionDetail = promotiondetailRepository.findById(id);

        if (!existingPromotionDetail.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (dto.getPromotionID() == null || dto.getPromotionID().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Promotion ID must not be null or empty.");
        }

        Optional<Promotion> promotion = promotionRepository.findById(dto.getPromotionID());
        if (!promotion.isPresent()) {
            return ResponseEntity.badRequest().body("Promotion with ID " + dto.getPromotionID() + " does not exist.");
        }

        if (dto.getPercentDiscount() == null || dto.getPercentDiscount() < 0) {
            return ResponseEntity.badRequest().body("Percent discount must be a non-negative integer.");
        }

        Promotiondetail promotionDetail = existingPromotionDetail.get();
        promotionDetail.setPromotionID(promotion.get());
        promotionDetail.setPercentDiscount(dto.getPercentDiscount());

        promotiondetailRepository.save(promotionDetail);
        return ResponseEntity.ok("Promotion detail updated successfully.");
    }

    // 5. Delete PromotionDetail
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePromotionDetail(@PathVariable String id) {
        if (!promotiondetailRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            promotiondetailRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting promotion detail: " + e.getMessage());
        }
    }
}
