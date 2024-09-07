package com.datn.endless.controllers;

import com.datn.endless.entities.Brand;
import com.datn.endless.repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandRepository brandRepository;

    // Lấy tất cả brands
    @GetMapping
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    // Lấy 1 brand theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable String id) {
        Optional<Brand> brand = brandRepository.findById(id);
        return brand.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create 1 brand mới
    @PostMapping
    public ResponseEntity<Brand> createBrand(@RequestBody Brand brand) {
        Brand savedBrand = brandRepository.save(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBrand);
    }

    // Update brand đã tồn tại
    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable String id, @RequestBody Brand brand) {
        if (!brandRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        brand.setBrandID(id);
        Brand updatedBrand = brandRepository.save(brand);
        return ResponseEntity.ok(updatedBrand);
    }

    // Delete 1 brand theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable String id) {
        if (!brandRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        brandRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
