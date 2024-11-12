package com.datn.endless.controllers;

import com.datn.endless.dtos.ErrorResponse;
import com.datn.endless.dtos.PromotionDTO;
import com.datn.endless.services.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public ResponseEntity<Page<PromotionDTO>> getAllPromotions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PromotionDTO> promotions = promotionService.findPromotionsByCriteria(name, startDate, endDate, pageable);

        return ResponseEntity.ok(promotions);
    }

    @PostMapping
    public ResponseEntity<PromotionDTO> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO) {
        PromotionDTO createdPromotion = promotionService.createPromotion(promotionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionDTO> updatePromotion(
            @PathVariable String id,
            @Valid @RequestBody PromotionDTO promotionDTO) {
        PromotionDTO updatedPromotion = promotionService.updatePromotion(id, promotionDTO);
        return ResponseEntity.ok(updatedPromotion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ErrorResponse> deletePromotion(@PathVariable String id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok(new ErrorResponse("Xóa thành công", "Đã xóa promotion với ID " + id));
    }


}
