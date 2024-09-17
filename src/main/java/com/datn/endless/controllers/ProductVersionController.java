package com.datn.endless.controllers;

import com.datn.endless.dtos.ProductVersionDTO;
import com.datn.endless.entities.Productversion;
import com.datn.endless.repositories.ProductRepository;
import com.datn.endless.repositories.ProductversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productversions")
public class ProductVersionController {

    @Autowired
    private ProductversionRepository productversionRepository;

    @Autowired
    private ProductRepository productRepository;

    // Create a new product version
    @PostMapping
    public ResponseEntity<String> createProductVersion(@RequestBody ProductVersionDTO productVersionDTO) {
        // Validate image file extension
        if (productVersionDTO.getImage() != null && !productVersionDTO.getImage().matches(".*\\.(jpg|jpeg|png)$")) {
            return ResponseEntity.badRequest().body("Invalid image file format. Only .jpg, .jpeg, or .png are allowed.");
        }

        Productversion productversion = new Productversion();
        productversion.setVersionName(productVersionDTO.getVersionName());
        productversion.setPurchasePrice(productVersionDTO.getPurchasePrice());
        productversion.setPrice(productVersionDTO.getPrice());
        productversion.setStatus(productVersionDTO.getStatus());
        productversion.setImage(productVersionDTO.getImage());

        // Check if product exists
        if (!productRepository.existsById(productVersionDTO.getProductVersionID())) {
            return ResponseEntity.badRequest().body("Product not found with ID: " + productVersionDTO.getProductVersionID());
        }

        productversion.setProductID(productRepository.findById(productVersionDTO.getProductVersionID()).get());

        try {
            productversionRepository.save(productversion);
            return ResponseEntity.ok("Product version created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating product version: " + e.getMessage());
        }
    }

    // Get all product versions
    @GetMapping
    public ResponseEntity<List<ProductVersionDTO>> getAllProductVersions() {
        List<Productversion> productVersions = productversionRepository.findAll();
        List<ProductVersionDTO> productVersionDTOs = productVersions.stream().map(productversion -> {
            ProductVersionDTO dto = new ProductVersionDTO();
            dto.setProductVersionID(productversion.getProductVersionID());
            dto.setVersionName(productversion.getVersionName());
            dto.setPurchasePrice(productversion.getPurchasePrice());
            dto.setPrice(productversion.getPrice());
            dto.setStatus(productversion.getStatus());
            dto.setImage(productversion.getImage());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(productVersionDTOs);
    }

    // Get product version by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductVersionDTO> getProductVersionById(@PathVariable String id) {
        Optional<Productversion> productVersion = productversionRepository.findById(id);
        if (productVersion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Productversion productversion = productVersion.get();
        ProductVersionDTO dto = new ProductVersionDTO();
        dto.setProductVersionID(productversion.getProductVersionID());
        dto.setVersionName(productversion.getVersionName());
        dto.setPurchasePrice(productversion.getPurchasePrice());
        dto.setPrice(productversion.getPrice());
        dto.setStatus(productversion.getStatus());
        dto.setImage(productversion.getImage());

        return ResponseEntity.ok(dto);
    }

    // Update a product version
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProductVersion(@PathVariable String id, @RequestBody ProductVersionDTO productVersionDTO) {
        Optional<Productversion> existingProductVersion = productversionRepository.findById(id);
        if (existingProductVersion.isEmpty()) {
            return ResponseEntity.badRequest().body("Product version not found with ID: " + id);
        }

        // Validate image file extension
        if (productVersionDTO.getImage() != null && !productVersionDTO.getImage().matches(".*\\.(jpg|jpeg|png)$")) {
            return ResponseEntity.badRequest().body("Invalid image file format. Only .jpg, .jpeg, or .png are allowed.");
        }

        Productversion productversion = existingProductVersion.get();
        productversion.setVersionName(productVersionDTO.getVersionName());
        productversion.setPurchasePrice(productVersionDTO.getPurchasePrice());
        productversion.setPrice(productVersionDTO.getPrice());
        productversion.setStatus(productVersionDTO.getStatus());
        productversion.setImage(productVersionDTO.getImage());

        try {
            productversionRepository.save(productversion);
            return ResponseEntity.ok("Product version updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating product version: " + e.getMessage());
        }
    }

    // Delete a product version
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductVersion(@PathVariable String id) {
        if (!productversionRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Product version not found with ID: " + id);
        }

        try {
            productversionRepository.deleteById(id);
            return ResponseEntity.ok("Product version deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting product version: " + e.getMessage());
        }
    }

    // Search product versions by product name
    @GetMapping("/search")
    public ResponseEntity<List<ProductVersionDTO>> searchProductVersionsByProductName(@RequestParam String productName) {
        List<Productversion> productVersions = productversionRepository.findByProductNameContaining(productName);
        List<ProductVersionDTO> productVersionDTOs = productVersions.stream().map(productversion -> {
            ProductVersionDTO dto = new ProductVersionDTO();
            dto.setProductVersionID(productversion.getProductVersionID());
            dto.setVersionName(productversion.getVersionName());
            dto.setPurchasePrice(productversion.getPurchasePrice());
            dto.setPrice(productversion.getPrice());
            dto.setStatus(productversion.getStatus());
            dto.setImage(productversion.getImage());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(productVersionDTOs);
    }
}
