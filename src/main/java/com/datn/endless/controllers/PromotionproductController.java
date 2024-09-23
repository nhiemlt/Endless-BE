package com.datn.endless.controllers;

import com.datn.endless.entities.Promotionproduct;
import com.datn.endless.services.PromotionproductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/promotionproducts")
public class PromotionproductController {

    @Autowired
    private PromotionproductService promotionproductService;

    @GetMapping
    public ResponseEntity<List<Promotionproduct>> getAllPromotionproducts() {
        List<Promotionproduct> promotionproducts = promotionproductService.getAllPromotionproducts();
        return new ResponseEntity<>(promotionproducts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promotionproduct> getPromotionproductById(@PathVariable("id") String id) {
        Optional<Promotionproduct> promotionproduct = promotionproductService.getPromotionproductById(id);
        return promotionproduct.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Promotionproduct> createPromotionproduct(@RequestBody Promotionproduct promotionproduct) {
        Promotionproduct createdPromotionproduct = promotionproductService.createPromotionproduct(promotionproduct);
        return new ResponseEntity<>(createdPromotionproduct, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promotionproduct> updatePromotionproduct(@PathVariable("id") String id,
                                                                   @RequestBody Promotionproduct promotionproduct) {
        Promotionproduct updatedPromotionproduct = promotionproductService.updatePromotionproduct(id, promotionproduct);
        return updatedPromotionproduct != null ? new ResponseEntity<>(updatedPromotionproduct, HttpStatus.OK) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotionproduct(@PathVariable("id") String id) {
        boolean isDeleted = promotionproductService.deletePromotionproduct(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
