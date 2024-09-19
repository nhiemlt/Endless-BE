package com.datn.endless.controllers;

import com.datn.endless.entities.Brand;
import com.datn.endless.entities.Category;
import com.datn.endless.repositories.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    private BrandRepository brandRepository;

    // Kiểm tra định dạng file ảnh hợp lệ
    private boolean isValidImageFormat(String fileName) {
        String regex = "([^\\s]+(\\.(?i)(jpg|jpeg|png|gif|bmp))$)";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(fileName).matches();
    }

    // 1. Create a new brand
    @PostMapping
    public ResponseEntity<String> createBrand(@RequestBody Brand brand) {
        if (brand.getName() == null || brand.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Brand name cannot be empty.");
        }
        if (brand.getLogo() != null && !isValidImageFormat(brand.getLogo())) {
            return ResponseEntity.badRequest().body("Invalid logo format. Only image files are allowed.");
        }

        // Kiểm tra nếu tên brand đã tồn tại
        Optional<Brand> existingBrand = brandRepository.findByName(brand.getName());
        if (existingBrand.isPresent()) {
            return ResponseEntity.badRequest().body("Brand name already exists.");
        }

        // Lưu brand
        Brand savedBrand = brandRepository.save(brand);
        return ResponseEntity.ok("Brand created successfully.");
    }

    // 2. Get all brands
    @GetMapping
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    // 3. Get a brand by ID
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable String id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent()) {
            return ResponseEntity.ok(brand.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. Update a brand
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBrand(
            @PathVariable String id,
            @RequestBody Brand updatedBrand) {
        Optional<Brand> existingBrand = brandRepository.findById(id);

        if (existingBrand.isPresent()) {
            Brand brand = existingBrand.get();

            // Kiểm tra trùng tên khi cập nhật
            if (!brand.getName().equals(updatedBrand.getName())) {
                Optional<Brand> brandWithSameName = brandRepository.findByName(updatedBrand.getName());
                if (brandWithSameName.isPresent()) {
                    return ResponseEntity.badRequest().body("Brand name already exists.");
                }
            }

            if (updatedBrand.getName() == null || updatedBrand.getName().isEmpty()) {
                return ResponseEntity.badRequest().body("Brand name cannot be empty.");
            }

            if (updatedBrand.getLogo() != null && !isValidImageFormat(updatedBrand.getLogo())) {
                return ResponseEntity.badRequest().body("Invalid logo format. Only image files are allowed.");
            }

            brand.setName(updatedBrand.getName());
            brand.setLogo(updatedBrand.getLogo());
            brandRepository.save(brand);
            return ResponseEntity.ok("Brand updated successfully.");
        } else {
            return ResponseEntity.status(404).body("Brand not found with ID: " + id);
        }
    }

    // 5. Delete a brand
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable String id) {
        if (brandRepository.existsById(id)) {
            brandRepository.deleteById(id);
            return ResponseEntity.ok("Brand deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Brand not found with ID: " + id);
        }
    }

    // 6. Get category by name
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getCategoryByName(@PathVariable String name) {
        Optional<Brand> brand = brandRepository.findByName(name);
        if (brand.isPresent()) {
            return ResponseEntity.ok(brand.get());
        } else {
            return ResponseEntity.status(404).body("Brand not found with name: " + name);
        }
    }
}
