package com.datn.endless.controllers;

import com.datn.endless.entities.Product;
import com.datn.endless.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // Create a new product
    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody Product product) {
        if (product.getName() == null || product.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Product name cannot be empty.");
        }

        // Set default UUID if not provided
        if (product.getProductID() == null || product.getProductID().isEmpty()) {
            product.setProductID(UUID.randomUUID().toString());
        }

        try {
            productRepository.save(product);
            return ResponseEntity.ok("Product created successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating product: " + e.getMessage());
        }
    }

    // Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return ResponseEntity.status(404).body(null); // No products found
        }
        return ResponseEntity.ok(products);
    }

    // Get a product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return ResponseEntity.status(404).body(null); // Product not found
        }
        return ResponseEntity.ok(product.get());
    }

    // Search products by name
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductByName(@RequestParam String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        if (products.isEmpty()) {
            return ResponseEntity.status(404).body(null); // No products found with the given name
        }
        return ResponseEntity.ok(products);
    }

    // Update a product
    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable String id, @RequestBody Product product) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Product not found with ID: " + id);
        }

        if (product.getName() == null || product.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Product name cannot be empty.");
        }

        product.setProductID(id); // Ensure ID is correct
        try {
            productRepository.save(product);
            return ResponseEntity.ok("Product updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating product: " + e.getMessage());
        }
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Product not found with ID: " + id);
        }

        try {
            productRepository.deleteById(id);
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting product: " + e.getMessage());
        }
    }
}
