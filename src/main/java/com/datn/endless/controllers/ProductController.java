package com.datn.endless.controllers;

import com.datn.endless.dtos.ProductDTO;
import com.datn.endless.models.ProductModel;
import com.datn.endless.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Endpoint: Lấy danh sách sản phẩm hoặc thông tin chi tiết của một sản phẩm
    @GetMapping({ "", "/{id}" })
    public ResponseEntity<?> getProductsOrProductById(
            @RequestParam(required = false) String keyword,
            @PathVariable(required = false) String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoryID,
            @RequestParam(required = false) String brandID,
            @RequestParam(defaultValue = "createDate") String sortBy, // Thêm tham số sortBy
            @RequestParam(defaultValue = "asc") String direction) { // Thêm tham số direction

        // Nếu có ID
        if (id != null) {
            return productService.getProductById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            // Nếu không có ID, lấy danh sách sản phẩm với các tiêu chí
            Page<ProductDTO> productPage = productService.getProducts(keyword, page, size, categoryID, brandID, sortBy, direction);
            return ResponseEntity.ok(productPage);
        }
    }



    // Endpoint: Tạo mới sản phẩm
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductModel productModel) {
        ProductDTO createdProduct = productService.createProduct(productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct); // Trả về mã 201
    }


    // Endpoint: Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable String id, @Valid @RequestBody ProductModel productModel) {
        ProductDTO updatedProduct = productService.updateProduct(id, productModel);
        return ResponseEntity.ok(updatedProduct);
    }

    // Endpoint: Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Xóa sản phẩm thành công.");
    }
}
