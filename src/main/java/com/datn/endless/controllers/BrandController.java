package com.datn.endless.controllers;

import com.datn.endless.dtos.BrandDTO;
import com.datn.endless.models.BrandModel;
import com.datn.endless.models.UserModel;
import com.datn.endless.services.BrandService;
import com.datn.endless.utils.ErrorResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;


    private boolean isValidBase64(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return false;
        }

        // Kiểm tra xem có chứa phần header "data:image" hay không
        if (base64.startsWith("data:image")) {
            // Tách phần header và chỉ giữ lại phần base64
            String[] parts = base64.split(",");
            if (parts.length == 2) {
                base64 = parts[1]; // Phần mã base64 không có header
            } else {
                return false; // Không hợp lệ nếu không có mã base64 sau dấu phẩy
            }
        }

        // Kiểm tra định dạng base64
        return base64.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
    }

    // Tạo mới một brand
    @PostMapping
    public ResponseEntity<?> createBrand(@RequestBody BrandModel brandModel) {

        // Kiểm tra định dạng base64 cho avatar
        if (!isValidBase64(brandModel.getLogo())) {
            return ResponseEntity.badRequest().body(new ErrorResponse(List.of("Invalid logo format: The logo must be a valid base64 string.")));
        }
        BrandDTO createdBrand = brandService.createBrand(brandModel);
        return ResponseEntity.ok(createdBrand);
    }

    // Cập nhật brand theo ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable String id ,@RequestParam String name, @RequestParam String logo) {
        BrandModel brandModel = new BrandModel();
        brandModel.setName(name);
        // Kiểm tra định dạng base64 cho avatar
        if (!isValidBase64(brandModel.getLogo())) {
            return ResponseEntity.badRequest().body(new ErrorResponse(List.of("Invalid logo format: The logo must be a valid base64 string.")));

        }
        BrandDTO updatedBrand = brandService.updateBrand(id, brandModel);
        return ResponseEntity.ok(updatedBrand);
    }

    // Lấy tất cả các brand
    @GetMapping
    public ResponseEntity<List<BrandDTO>> getAllBrands() {
        List<BrandDTO> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }

    // Lấy brand theo ID
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable String id) {
        Optional<BrandDTO> brand = brandService.getBrandById(id);
        return brand.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Xóa brand theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable String id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
