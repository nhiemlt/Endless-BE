package com.datn.endless.controllers;

import com.datn.endless.dtos.ProductDTO;
import com.datn.endless.models.ProductModel;
import com.datn.endless.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductModel productModel) {
        ProductDTO createdProduct = productService.createProduct(productModel);
        return ResponseEntity.ok(createdProduct);
    }

    @GetMapping
    public ResponseEntity<?> getProductsOrProductById(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (id != null && !id.isEmpty()) {
            // Nếu id được truyền, trả về thông tin chi tiết của một sản phẩm
            return productService.getProductById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } else {
            // Nếu không có id, trả về danh sách sản phẩm với filter
            List<ProductDTO> products = productService.getProducts(name, categoryId, brandId, page, size);
            return ResponseEntity.ok(products);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable String id, @RequestBody ProductModel productModel) {
        ProductDTO updatedProduct = productService.updateProduct(id, productModel);
        return ResponseEntity.ok(updatedProduct);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully.");
    }
}
