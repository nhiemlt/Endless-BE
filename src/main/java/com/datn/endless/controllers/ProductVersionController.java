package com.datn.endless.controllers;

import com.datn.endless.dtos.FilterRequest;
import com.datn.endless.dtos.ProductVersionDTO;
import com.datn.endless.entities.Productversion;
import com.datn.endless.exceptions.ProductVersionInactiveException;
import com.datn.endless.models.ProductVersionModel;
import com.datn.endless.services.ProductService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-versions")
public class ProductVersionController {

    @Autowired
    private ProductVersionService productVersionService;
    @Autowired
    private ProductService productService;

    @GetMapping("/get-user")
    public ResponseEntity<Page<ProductVersionDTO>> searchProductVersions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "versionName") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String keyword) {

        // Danh sách các thuộc tính hỗ trợ sắp xếp
        List<String> sortableFields = List.of("versionName", "numberOfReviews", "discountPrice", "quantitySold");

        // Kiểm tra sortBy hợp lệ
        if (!sortableFields.contains(sortBy)) {
            return ResponseEntity.badRequest().body(null);
        }

        Page<ProductVersionDTO> productVersions = productVersionService.getProductVersionsByKeyword(page, size, sortBy, direction, keyword);
        return ResponseEntity.ok(productVersions);
    }


    // API tìm kiếm ProductVersion theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductVersionDTO> getProductVersionById(@PathVariable("id") String productVersionID) {
        // Gọi service để tìm kiếm ProductVersion theo ID
        ProductVersionDTO productVersionDTO = productVersionService.searchProductVersionById(productVersionID);

        return ResponseEntity.ok(productVersionDTO);
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

    // Lấy top 5 sản phẩm bán chạy nhất theo danh mục
    @GetMapping("/top5ByCategory/{categoryID}")
    public ResponseEntity<List<ProductVersionDTO>> getTop5BestSellingProductsByCategory(
            @PathVariable String categoryID) {
        List<ProductVersionDTO> topProducts = productVersionService.getTop5BestSellingProductsByCategory(categoryID);
        return ResponseEntity.ok(topProducts);
    }

    // Lấy top 5 sản phẩm bán chạy nhất theo thương hiệu
    @GetMapping("/top5ByBrand/{brandID}")
    public ResponseEntity<List<ProductVersionDTO>> getTop5BestSellingProductsByBrand(
            @PathVariable String brandID) {
        List<ProductVersionDTO> topProducts = productVersionService.getTop5BestSellingProductsByBrand(brandID);
        return ResponseEntity.ok(topProducts);
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


    // Endpoint lấy tất cả phiên bản sản phẩm theo thương hiệu
    @GetMapping("/brand/{brandName}")
    public ResponseEntity<List<ProductVersionDTO>> getProductVersionsByBrand(@PathVariable String brandName) {
        List<ProductVersionDTO> productVersions = productVersionService.getProductVersionsByBrand(brandName);
        return ResponseEntity.ok(productVersions);
    }

    // Đếm số lượng sản phẩm
    @GetMapping("/count-products")
    public ResponseEntity<String> countProducts() {
        long productCount = productVersionService.countProducts();
        String message = "Tổng số lượng sản phẩm hiện có: " + productCount;
        return ResponseEntity.ok(message);
    }

    // Đếm số lượng brand
    @GetMapping("/count-brands")
    public ResponseEntity<String> countBrands() {
        long brandCount = productVersionService.countBrands();
        String message = "Tổng số lượng thương hiệu hiện có: " + brandCount;
        return ResponseEntity.ok(message);
    }


    @GetMapping("/sorted")
    public ResponseEntity<List<ProductVersionDTO>> getSortedProductVersions(
            @RequestParam String sortBy,
            @RequestParam String direction) {
        try {
            List<ProductVersionDTO> sortedProductVersions = productVersionService.getSortedProductVersions(sortBy, direction);
            return ResponseEntity.ok(sortedProductVersions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}

