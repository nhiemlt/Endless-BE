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
    // Tạo mới ProductVersion
    @PostMapping
    public ResponseEntity<ProductVersionDTO> createProductVersion(
            @RequestParam String productID,
            @RequestParam String versionName,
            @RequestParam double price,
            @RequestParam double purchasePrice,
            @RequestParam double weight,
            @RequestParam double height,
            @RequestParam double length,
            @RequestParam double width,
            @RequestParam String image,
            @RequestParam List<String> attributeValueID) { // Thêm attributeValueID

        ProductVersionModel model = new ProductVersionModel();
        model.setProductID(productID);
        model.setVersionName(versionName);
        model.setPrice(BigDecimal.valueOf(price));
        model.setPurchasePrice(BigDecimal.valueOf(purchasePrice));
        model.setWeight(BigDecimal.valueOf(weight));
        model.setHeight(BigDecimal.valueOf(height));
        model.setLength(BigDecimal.valueOf(length));
        model.setWidth(BigDecimal.valueOf(width));
        model.setImage(image);
        model.setAttributeValueID(attributeValueID);

        ProductVersionDTO createdProductVersion = productVersionService.createProductVersion(model);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProductVersion);
    }

    // Cập nhật ProductVersion
    @PutMapping("/{id}")
    public ResponseEntity<ProductVersionDTO> updateProductVersion(
            @PathVariable("id") String productVersionID,
            @RequestParam String productID,
            @RequestParam String versionName,
            @RequestParam double price,
            @RequestParam double purchasePrice,
            @RequestParam double weight,
            @RequestParam double height,
            @RequestParam double length,
            @RequestParam double width,
            @RequestParam String image,
            @RequestParam List<String> attributeValueID) {

        ProductVersionModel model = new ProductVersionModel();
        model.setProductID(productID);
        model.setVersionName(versionName);
        model.setPrice(BigDecimal.valueOf(price));
        model.setPurchasePrice(BigDecimal.valueOf(purchasePrice));
        model.setWeight(BigDecimal.valueOf(weight));
        model.setHeight(BigDecimal.valueOf(height));
        model.setLength(BigDecimal.valueOf(length));
        model.setWidth(BigDecimal.valueOf(width));
        model.setImage(image);
        model.setAttributeValueID(attributeValueID);

        ProductVersionDTO updatedProductVersion = productVersionService.updateProductVersion(productVersionID, model);
        return ResponseEntity.ok(updatedProductVersion);
    }

    // Xóa ProductVersion theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductVersion(@PathVariable("id") String productVersionID) {
        productVersionService.deleteProductVersion(productVersionID);
        return ResponseEntity.noContent().build();
    }
}

