package com.datn.endless.controllers;

import com.datn.endless.dtos.PromotionproductDTO;
import com.datn.endless.models.PromotionProductModel;
import com.datn.endless.services.PromotionProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/promotion-products")
public class PromotionProductController {

    @Autowired
    private PromotionProductService promotionProductService;


    @PostMapping
    public ResponseEntity<List<PromotionproductDTO>> createPromotionProduct(
            @RequestBody PromotionProductModel request) {

        List<PromotionproductDTO> createdProducts = promotionProductService.createPromotionProduct(
                request.getPromotionDetailID(),
                request.getProductVersionIDs()
        );

        return ResponseEntity.ok(createdProducts);
    }
    // Cập nhật PromotionProduct theo ID
    @PutMapping("/{id}")
    public ResponseEntity<PromotionproductDTO> updatePromotionProduct(
            @PathVariable String id,
            @RequestBody PromotionProductModel promotionProductModel) {
        PromotionproductDTO updatedProduct = promotionProductService.updatePromotionProduct(id, promotionProductModel);
        return ResponseEntity.ok(updatedProduct);
    }
    // Lấy tất cả PromotionProducts với phân trang và lọc theo percentDiscount
    @GetMapping
    public ResponseEntity<Page<PromotionproductDTO>> getAllPromotionProducts(
            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại
            @RequestParam(defaultValue = "10") int size, // Kích thước trang
            @RequestParam(required = false) Double percentDiscount) { // Phần trăm giảm giá (tùy chọn)

        Pageable pageable = PageRequest.of(page, size);  // Tạo đối tượng Pageable
        Page<PromotionproductDTO> promotionproductDTOs = promotionProductService.getAllPromotionProducts(pageable, percentDiscount);

        return ResponseEntity.ok(promotionproductDTOs);
    }


    // Xóa PromotionProduct theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotionProduct(@PathVariable String id) {
        promotionProductService.deletePromotionProduct(id);
        return ResponseEntity.noContent().build();
    }
}
