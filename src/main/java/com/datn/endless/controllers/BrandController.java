package com.datn.endless.controllers;

import com.datn.endless.dtos.BrandDTO;
import com.datn.endless.entities.Brand;
import com.datn.endless.models.BrandModel;
import com.datn.endless.repositories.BrandRepository;
import com.datn.endless.services.BrandService;
import com.datn.endless.utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandRepository brandRepository;

    private BrandDTO convertToDTO(Brand brand) {
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setBrandID(brand.getBrandID());
        brandDTO.setBrandName(brand.getName());
        brandDTO.setLogo(brand.getLogo());
        return brandDTO;
    }

    // 1. Tạo brand mới
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<String> createBrand(
            @RequestParam("name") String name,
            @RequestParam("logo") MultipartFile logo) {

        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body("Brand name cannot be empty.");
        }

        String logoBase64;
        try {
            logoBase64 = ImageUtil.convertToBase64(logo);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Could not convert logo to Base64.");
        }

        Brand newBrand = new Brand();
        newBrand.setBrandID(UUID.randomUUID().toString());
        newBrand.setName(name);
        newBrand.setLogo(logoBase64);

        brandRepository.save(newBrand);
        return ResponseEntity.ok("Brand created successfully.");
    }

    // 2. Lấy tất cả brand, kèm phân trang, tìm kiếm, filter theo ID hoặc tên
    @GetMapping
    public ResponseEntity<?> getBrands(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (id != null && !id.isEmpty()) {
            Optional<Brand> brand = brandRepository.findById(id);
            return brand.isPresent() ? ResponseEntity.ok(convertToDTO(brand.get()))
                    : ResponseEntity.status(404).body("Brand not found with ID: " + id);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Brand> allBrands;

        if (name != null && !name.isEmpty()) {
            allBrands = brandRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            allBrands = brandRepository.findAll(pageable);
        }

        List<BrandDTO> brandDTOs = allBrands.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(brandDTOs);
    }

    // 3. Cập nhật brand
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBrand(
            @PathVariable String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) MultipartFile logo) {

        Optional<Brand> existingBrandOpt = brandRepository.findById(id);

        if (!existingBrandOpt.isPresent()) {
            return ResponseEntity.status(404).body("Brand not found with ID: " + id);
        }

        Brand existingBrand = existingBrandOpt.get();

        // Kiểm tra tên thương hiệu mới
        if (name != null && !name.isEmpty()) {
            existingBrand.setName(name);
        }

        // Kiểm tra logo mới
        if (logo != null && !logo.isEmpty()) {
            try {
                existingBrand.setLogo(ImageUtil.convertToBase64(logo));
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Could not convert logo to Base64.");
            }
        }

        brandRepository.save(existingBrand);
        return ResponseEntity.ok("Brand updated successfully.");
    }


    // 4. Xóa brand
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable String id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Brand deleted successfully.");
    }
}
