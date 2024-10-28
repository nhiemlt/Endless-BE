package com.datn.endless.controllers;

import com.datn.endless.dtos.BrandDTO;
import com.datn.endless.models.BrandModel;
import com.datn.endless.services.BrandService;
import com.datn.endless.utils.ErrorResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin(origins = "*") // Hỗ trợ CORS nếu cần
public class BrandController {

    @Autowired
    private BrandService brandService;

    // Tạo mới một brand
    @PostMapping
    public ResponseEntity<?> createBrand(@Valid @RequestBody BrandModel brandModel, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Đầu vào không hợp lệ", result.getAllErrors()));
        }
        try {
            BrandDTO createdBrand = brandService.createBrand(brandModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Cập nhật brand theo ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable String id, @Valid @RequestBody BrandModel brandModel, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Đầu vào không hợp lệ", result.getAllErrors()));
        }
        try {
            BrandDTO updatedBrand = brandService.updateBrand(id, brandModel);
            return ResponseEntity.ok(updatedBrand);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Lấy tất cả các brand
    @GetMapping
    public ResponseEntity<Page<BrandDTO>> getAllBrands(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BrandDTO> brands = brandService.getBrandsWithPaginationAndSearch(keyword, pageable);
        return ResponseEntity.ok(brands);
    }

    // Lấy brand theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable String id) {
        Optional<BrandDTO> brand = brandService.getBrandById(id);
        if (brand.isPresent()) {
            return ResponseEntity.ok(brand.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Không tìm thấy thương hiệu có ID: " + id));
        }
    }
    @GetMapping("/sreach")
    public ResponseEntity<?> searchBrandsByName(@RequestParam String name) {
        List<BrandDTO> brands = brandService.getBrandsByName(name);
        if (brands.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Không tìm thấy thương hiệu nào phù hợp: " + name));
        }
        return ResponseEntity.ok(brands);
    }


    // Xóa brand theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable String id) {
        try {
            brandService.deleteBrand(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}
