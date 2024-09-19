package com.datn.endless.controllers;

import com.datn.endless.entities.Category;
import com.datn.endless.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // 1. Create a new category
    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        if (category.getName() == null || category.getName().isEmpty()) {
            return ResponseEntity.badRequest().body("Category name cannot be empty.");
        }

        // Kiểm tra nếu tên danh mục đã tồn tại
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
        if (existingCategory.isPresent()) {
            return ResponseEntity.badRequest().body("Category name already exists.");
        }

        Category savedCategory = categoryRepository.save(category);
        return ResponseEntity.ok("Category created successfully.");
    }


    // 2. Get all categories
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 3. Get category by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable String id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            // Bắt lỗi ID không tồn tại
            return ResponseEntity.status(404).body("Category not found with ID: " + id);
        }
    }

    // 4. Update category
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable String id,
            @RequestBody Category updatedCategory) {
        Optional<Category> existingCategory = categoryRepository.findById(id);

        if (existingCategory.isPresent()) {
            Category category = existingCategory.get();

            // Kiểm tra nếu tên danh mục mới đã tồn tại (trừ trường hợp trùng với chính danh mục hiện tại)
            if (!category.getName().equals(updatedCategory.getName())) {
                Optional<Category> categoryWithSameName = categoryRepository.findByName(updatedCategory.getName());
                if (categoryWithSameName.isPresent()) {
                    return ResponseEntity.badRequest().body("Category name already exists.");
                }
            }

            if (updatedCategory.getName() == null || updatedCategory.getName().isEmpty()) {
                return ResponseEntity.badRequest().body("Category name cannot be empty.");
            }

            category.setName(updatedCategory.getName());
            if (updatedCategory.getEnName() != null) {
                category.setEnName(updatedCategory.getEnName());
            }
            categoryRepository.save(category);
            return ResponseEntity.ok("Category updated successfully.");
        } else {
            return ResponseEntity.status(404).body("Category not found with ID: " + id);
        }
    }


    // 5. Delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable String id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok("Category deleted successfully.");
        } else {
            // Bắt lỗi ID không tồn tại
            return ResponseEntity.status(404).body("Category not found with ID: " + id);
        }
    }
    // 6. Get category by name
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getCategoryByName(@PathVariable String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        } else {
            return ResponseEntity.status(404).body("Category not found with name: " + name);
        }
    }
}
