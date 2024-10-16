package com.datn.endless.controllers;

import com.datn.endless.dtos.PromotionproductDTO;
import com.datn.endless.models.PromotionProductModel;
import com.datn.endless.services.PromotionProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/promotion-products")
public class PromotionProductController {

    @Autowired
    private PromotionProductService promotionProductService;

    // Tạo mới một PromotionProduct
    @PostMapping
    public ResponseEntity<PromotionproductDTO> createPromotionProduct(@RequestParam String promotionDetailID, @RequestParam String productVersionID) {
        PromotionProductModel promotionProductModel = new PromotionProductModel();
        promotionProductModel.setPromotionDetailID(promotionDetailID);
        promotionProductModel.setProductVersionID(productVersionID);
        PromotionproductDTO createdProduct = promotionProductService.createPromotionProduct(promotionProductModel);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionproductDTO> updatePromotionProduct(
            @PathVariable String id,
            @RequestParam String promotionDetailID,
            @RequestParam String productVersionID) {
        PromotionProductModel promotionProductModel = new PromotionProductModel();
        promotionProductModel.setPromotionDetailID(promotionDetailID);
        promotionProductModel.setProductVersionID(productVersionID);
        PromotionproductDTO updatedProduct = promotionProductService.updatePromotionProduct(id, promotionProductModel);
        return ResponseEntity.ok(updatedProduct);
    }

    // Lấy tất cả PromotionProducts
    @GetMapping
    public ResponseEntity<List<PromotionproductDTO>> getAllPromotionProducts() {
        List<PromotionproductDTO> products = promotionProductService.getAllPromotionProducts();
        return ResponseEntity.ok(products);
    }

    // Lấy PromotionProduct theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PromotionproductDTO> getPromotionProductById(@PathVariable String id) {
        Optional<PromotionproductDTO> product = promotionProductService.getPromotionProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Xóa PromotionProduct theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotionProduct(@PathVariable String id) {
        promotionProductService.deletePromotionProduct(id);
        return ResponseEntity.noContent().build();
    }
}
