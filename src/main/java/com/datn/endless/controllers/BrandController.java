package com.datn.endless.controllers;

import com.datn.endless.dtos.BrandDTO;
import com.datn.endless.models.BrandModel;
import com.datn.endless.services.BrandService;
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

    // Tạo mới một brand
    @PostMapping
    public ResponseEntity<BrandDTO> createBrand(@RequestParam String name, @RequestParam MultipartFile logo) {
        BrandModel brandModel = new BrandModel();
        brandModel.setName(name);
        brandModel.setLogo(logo);
        BrandDTO createdBrand = brandService.createBrand(brandModel);
        return ResponseEntity.ok(createdBrand);
    }

    // Cập nhật brand theo ID
    @PutMapping("/{id}")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable String id ,@RequestParam String name, @RequestParam MultipartFile logo) {
        BrandModel brandModel = new BrandModel();
        brandModel.setName(name);
        brandModel.setLogo(logo);
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
