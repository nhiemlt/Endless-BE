package com.datn.endless.controllers;

import com.datn.endless.dtos.ProductVersionDTO;
import com.datn.endless.models.ProductVersionModel;
import com.datn.endless.services.ProductVersionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-versions")
public class ProductVersionController {

    @Autowired
    private ProductVersionService productVersionService;

//    // Lấy tất cả ProductVersions
//    @GetMapping
//    public ResponseEntity<List<ProductVersionDTO>> getAllProductVersions() {
//        List<ProductVersionDTO> productVersions = productVersionService.getAllProductVersions();
//        return ResponseEntity.ok(productVersions);
//    }

    // Lấy ProductVersion theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductVersionDTO> getProductVersionById(@PathVariable("id") String productVersionID) {
        ProductVersionDTO productVersion = productVersionService.getProductVersionById(productVersionID);
        return ResponseEntity.ok(productVersion);
    }

    @GetMapping
    public ResponseEntity<Page<ProductVersionDTO>> getAllProductVersions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "versionName") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String versionName) {
        Page<ProductVersionDTO> productVersions = productVersionService.getProductVersions(page, size, sortBy, direction, versionName);
        return ResponseEntity.ok(productVersions);
    }

    // Tạo mới ProductVersion
    @PostMapping
    public ResponseEntity<ProductVersionDTO> createProductVersion(@Valid @RequestBody ProductVersionModel productVersionModel) {
        ProductVersionDTO createdProductVersion = productVersionService.createProductVersion(productVersionModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductVersion);
    }

    // Cập nhật ProductVersion
    @PutMapping("/{id}")
    public ResponseEntity<ProductVersionDTO> updateProductVersion(
            @PathVariable("id") String productVersionID,
            @Valid @RequestBody ProductVersionModel productVersionModel) {
        ProductVersionDTO updatedProductVersion = productVersionService.updateProductVersion(productVersionID, productVersionModel);
        return ResponseEntity.ok(updatedProductVersion);
    }

    // Xóa ProductVersion theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductVersion(@PathVariable("id") String productVersionID) {
        productVersionService.deleteProductVersion(productVersionID);
        return ResponseEntity.noContent().build();
    }
}

