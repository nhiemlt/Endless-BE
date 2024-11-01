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
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/product-versions")
public class ProductVersionController {

    @Autowired
    private ProductVersionService productVersionService;

    @GetMapping("/by-product")
    public ResponseEntity<List<ProductVersionDTO>> getProductVersionsByProductId(@RequestParam String productID) {
        List<ProductVersionDTO> productVersions = productVersionService.getProductVersionsByProductId(productID);
        return ResponseEntity.ok(productVersions);
    }


    @GetMapping
    public ResponseEntity<?> getProductVersions(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String versionName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "versionName") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        if (id != null) {
            // Lấy ProductVersion theo ID
            ProductVersionDTO productVersion = productVersionService.getProductVersionById(id);
            return ResponseEntity.ok(productVersion);
        } else if (versionName != null) {
            // Lấy ProductVersions có phân trang, sort, tìm theo tên phiên bản
            Page<ProductVersionDTO> productVersions = productVersionService.getProductVersions(page, size, sortBy, direction, versionName);
            return ResponseEntity.ok(productVersions);
        } else {
            // Nếu không có tham số, trả về tất cả ProductVersions có phân trang và sort
            Page<ProductVersionDTO> productVersions = productVersionService.getProductVersions(page, size, sortBy, direction, null);
            return ResponseEntity.ok(productVersions);
        }
    }

    @PostMapping
    public ResponseEntity<ProductVersionDTO> createProductVersion(
            @Valid @RequestBody ProductVersionModel model) {

        ProductVersionDTO createdProductVersion = productVersionService.createProductVersion(model);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductVersion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductVersionDTO> updateProductVersion(
            @PathVariable("id") String productVersionID,
            @Valid @RequestBody ProductVersionModel model) {

        ProductVersionDTO updatedProductVersion = productVersionService.updateProductVersion(productVersionID, model);
        return ResponseEntity.ok(updatedProductVersion);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductVersion(@PathVariable("id") String productVersionID) {
        productVersionService.deleteProductVersion(productVersionID);
        // Thông báo khi xóa thành công
        return ResponseEntity.ok("Xóa phiên bản sản phẩm thành công.");
    }

}

