package com.datn.endless.controllers;

import com.datn.endless.dtos.FilterRequest;
import com.datn.endless.dtos.ProductVersionDTO;
import com.datn.endless.entities.Productversion;
import com.datn.endless.exceptions.ProductVersionInactiveException;
import com.datn.endless.models.ProductVersionModel;
import com.datn.endless.services.ProductVersionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/get-user")
    public ResponseEntity<Page<ProductVersionDTO>> searchProductVersions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "versionName") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String keyword) {

        Page<ProductVersionDTO> productVersions = productVersionService.getProductVersionsByKeyword(page, size, sortBy, direction, keyword);
        return ResponseEntity.ok(productVersions);
    }


    @PostMapping("/filter")
    public ResponseEntity<List<ProductVersionDTO>> filterProductVersions(
            @RequestBody FilterRequest filterRequest) {

        List<ProductVersionDTO> productVersions = productVersionService.filterProductVersionsByCategoriesAndBrands(
                filterRequest.getCategoryNames(),
                filterRequest.getBrandNames(),
                filterRequest.getMinPrice(),
                filterRequest.getMaxPrice());

        return ResponseEntity.ok(productVersions);
    }

    @GetMapping("/top-selling")
    public Page<ProductVersionDTO> getTopSellingProductVersions(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productVersionService.getTop5BestSellingProductVersionsThisMonth(pageable);
    }

    @GetMapping("/top-selling/all-time")
    public Page<ProductVersionDTO> getTopSellingProductVersionsAllTime(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productVersionService.getTop5BestSellingProductVersionsAllTime(pageable);
    }



    @GetMapping
    public ResponseEntity<?> getProductVersions(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "versionName") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(value = "keyword", required = false) String keyword) {


        // Nếu không có tham số, trả về tất cả ProductVersions có phân trang và sort
        Page<ProductVersionDTO> productVersions = productVersionService.getProductVersions(page, size, sortBy, direction, keyword);
        return ResponseEntity.ok(productVersions);
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

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateProductVersionStatus(
            @PathVariable("id") String productVersionID,
            @RequestParam String status) {

        // Cập nhật trạng thái phiên bản sản phẩm
        productVersionService.updateProductVersionStatus(productVersionID, status);

        // Gửi thông báo thành công với dòng trạng thái đã cập nhật
        return ResponseEntity.ok("Cập nhật trạng thái thành " + status + " cho phiên bản sản phẩm với ID: " + productVersionID);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductVersion(@PathVariable("id") String productVersionID) {
        productVersionService.deleteProductVersion(productVersionID);
        // Thông báo khi xóa thành công
        return ResponseEntity.ok("Xóa phiên bản sản phẩm thành công.");
    }



    @ExceptionHandler(ProductVersionInactiveException.class)
    public ResponseEntity<String> handleProductVersionInactiveException(ProductVersionInactiveException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}

