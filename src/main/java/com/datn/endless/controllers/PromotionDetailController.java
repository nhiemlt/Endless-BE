package com.datn.endless.controllers;

import com.datn.endless.dtos.PromotionDetailDTO;
import com.datn.endless.models.PromotionDetailModel;
import com.datn.endless.services.PromotionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/promotion-details")
public class PromotionDetailController {

    @Autowired
    private PromotionDetailService promotionDetailService;

    // Tạo mới một PromotionDetail
    @PostMapping
    public ResponseEntity<PromotionDetailDTO> createPromotionDetail(@RequestParam String promotionID, @RequestParam Integer percentDiscount) {
        PromotionDetailModel promotionDetailModel = new PromotionDetailModel();
        promotionDetailModel.setPromotionID(promotionID);
        promotionDetailModel.setPercentDiscount(percentDiscount);
        PromotionDetailDTO createdDetail = promotionDetailService.createPromotionDetail(promotionDetailModel);
        return ResponseEntity.ok(createdDetail);
    }

    // Cập nhật PromotionDetail theo ID
    @PutMapping("/{id}")
    public ResponseEntity<PromotionDetailDTO> updatePromotionDetail(@PathVariable String id, @RequestParam String promotionID, @RequestParam Integer percentDiscount) {
        PromotionDetailModel promotionDetailModel = new PromotionDetailModel();
        promotionDetailModel.setPromotionID(promotionID);
        promotionDetailModel.setPercentDiscount(percentDiscount);
        PromotionDetailDTO updatedDetail = promotionDetailService.updatePromotionDetail(id, promotionDetailModel);
        return ResponseEntity.ok(updatedDetail);
    }

    // Lấy tất cả PromotionDetails
    @GetMapping
    public ResponseEntity<List<PromotionDetailDTO>> getAllPromotionDetails() {
        List<PromotionDetailDTO> details = promotionDetailService.getAllPromotionDetails();
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
    public ResponseEntity<Void> deletePromotionDetail(@PathVariable String id) {
        promotionDetailService.deletePromotionDetail(id);
        return ResponseEntity.noContent().build();
    }
}
