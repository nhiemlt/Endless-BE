package com.datn.endless.controllers;

import com.datn.endless.dtos.ErrorResponse;
import com.datn.endless.dtos.PromotionDTO;
import com.datn.endless.models.PromotionModel;
import com.datn.endless.services.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO, BindingResult result) {
        // Kiểm tra lỗi hợp lệ
        if (result.hasErrors()) {
            // Lấy thông báo lỗi đầu tiên (hoặc bạn có thể xử lý để lấy thông báo lỗi khác nếu cần)
            String errorMessage = result.getFieldError() != null ? result.getFieldError().getDefaultMessage() : "Đầu vào không hợp lệ";

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Đầu vào không hợp lệ", errorMessage));
        }

        // Chuyển đổi PromotionDTO thành PromotionModel
        PromotionModel promotionModel = new PromotionModel();
        promotionModel.setName(promotionDTO.getName());
        promotionModel.setStartDate(promotionDTO.getStartDate());
        promotionModel.setEndDate(promotionDTO.getEndDate());
        promotionModel.setPoster(promotionDTO.getPoster());

        try {
            // Tạo mới promotion thông qua dịch vụ
            PromotionDTO createdPromotion = promotionService.createPromotion(promotionModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Lỗi", e.getMessage())); // Cung cấp tên lỗi
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(
            @PathVariable String id,
            @Valid @RequestBody PromotionDTO promotionDTO,
            BindingResult result) {

        // Kiểm tra lỗi hợp lệ
        if (result.hasErrors()) {
            // Lấy thông báo lỗi đầu tiên (hoặc bạn có thể xử lý để lấy thông báo lỗi khác nếu cần)
            String errorMessage = result.getFieldError() != null ? result.getFieldError().getDefaultMessage() : "Đầu vào không hợp lệ";

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Đầu vào không hợp lệ", errorMessage));
        }

        // Chuyển đổi PromotionDTO thành PromotionModel
        PromotionModel promotionModel = new PromotionModel();
        promotionModel.setName(promotionDTO.getName());
        promotionModel.setStartDate(promotionDTO.getStartDate());
        promotionModel.setEndDate(promotionDTO.getEndDate());
        promotionModel.setPoster(promotionDTO.getPoster());

        try {
            // Cập nhật promotion thông qua dịch vụ
            PromotionDTO updatedPromotion = promotionService.updatePromotion(id, promotionModel);
            return ResponseEntity.ok(updatedPromotion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Lỗi", e.getMessage())); // Cung cấp tên lỗi
        }
    }

    // Lấy tất cả các promotion hoặc lọc theo tiêu chí
    @GetMapping
    public ResponseEntity<Page<PromotionDTO>> getAllPromotions(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size); // Tạo pageable
        Page<PromotionDTO> promotions = promotionService.findPromotionsByCriteria(name, startDate, endDate, pageable);

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
    public ResponseEntity<?> deletePromotion(@PathVariable String id) {
        try {
            promotionService.deletePromotion(id);
            return ResponseEntity.ok(new ErrorResponse("Xóa promotion thành công", "Promotion với ID " + id + " đã được xóa.")); // Trả về thông báo thành công
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Không tìm thấy promotion", e.getMessage())); // Cung cấp thông báo lỗi rõ ràng
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Có lỗi xảy ra", e.getMessage())); // Xử lý các lỗi khác
        }
    }

}
