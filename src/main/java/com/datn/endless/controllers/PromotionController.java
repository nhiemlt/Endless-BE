package com.datn.endless.controllers;

import com.datn.endless.entities.Promotion;
import com.datn.endless.repositories.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionRepository promotionRepository;

    // Create a new promotion
    @PostMapping
    public ResponseEntity<String> createPromotion(@RequestBody Promotion promotion) {
        // Validate promotion data
        if (promotion.getName() == null || promotion.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Promotion name must not be null or empty.");
        }

        if (promotionRepository.existsByName(promotion.getName())) {
            return ResponseEntity.badRequest().body("Promotion with this name already exists.");
        }

        if (promotion.getStartDate() == null || promotion.getEndDate() == null) {
            return ResponseEntity.badRequest().body("Start date and end date must not be null.");
        }

        if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
            return ResponseEntity.badRequest().body("Start date must be before end date.");
        }

        // Set promotion ID if not already set
        if (promotion.getPromotionID() == null || promotion.getPromotionID().isEmpty()) {
            promotion.setPromotionID(UUID.randomUUID().toString());
        }

        Promotion createdPromotion = promotionRepository.save(promotion);
        return ResponseEntity.ok("Promotion created successfully.");
    }

    // Get all promotions
    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        List<Promotion> promotions = promotionRepository.findAll();
        return ResponseEntity.ok(promotions);
    }

    // Get promotion by ID
    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable String id) {
        Optional<Promotion> promotion = promotionRepository.findById(id);
        return promotion.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a promotion
    @PutMapping("/{id}")
    public ResponseEntity<String> updatePromotion(@PathVariable String id, @RequestBody Promotion promotion) {
        // Validate promotion data
        if (promotion.getName() == null || promotion.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Promotion name must not be null or empty.");
        }

        if (promotionRepository.existsByNameAndPromotionIDNot(promotion.getName(), id)) {
            return ResponseEntity.badRequest().body("Promotion with this name already exists.");
        }

        if (promotion.getStartDate() == null || promotion.getEndDate() == null) {
            return ResponseEntity.badRequest().body("Start date and end date must not be null.");
        }

        if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
            return ResponseEntity.badRequest().body("Start date must be before end date.");
        }

        if (!promotionRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Promotion not found with ID: " + id);
        }

        promotion.setPromotionID(id);
        promotionRepository.save(promotion);
        return ResponseEntity.ok("Promotion updated successfully.");
    }

    // Delete a promotion
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePromotion(@PathVariable String id) {
        if (!promotionRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Promotion not found with ID: " + id);
        }
        try {
            promotionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting promotion: " + e.getMessage());
        }
    }

    // Search promotions by name
    @GetMapping("/search")
    public ResponseEntity<List<Promotion>> searchPromotionsByName(@RequestParam String name) {
        List<Promotion> promotions = promotionRepository.findByNameContainingIgnoreCase(name);
        return ResponseEntity.ok(promotions);
    }
}
