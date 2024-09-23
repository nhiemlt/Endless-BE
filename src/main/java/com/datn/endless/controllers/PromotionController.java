// PromotionController.java
package com.datn.endless.controllers;

import com.datn.endless.dtos.PromotionDTO;
import com.datn.endless.services.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {
    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public Page<PromotionDTO> getAllPromotions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort) {
        return promotionService.getPromotions(name, startDate, endDate, page, size, sort);
    }

    @PostMapping
    public PromotionDTO createPromotion(@RequestBody PromotionDTO dto) {
        return promotionService.createPromotion(dto);
    }

    @PutMapping("/{id}")
    public PromotionDTO updatePromotion(@PathVariable String id, @RequestBody PromotionDTO dto) {
        return promotionService.updatePromotion(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletePromotion(@PathVariable String id) {
        promotionService.deletePromotion(id);
    }

}
