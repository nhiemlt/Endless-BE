package com.datn.endless.controllers;

import com.datn.endless.dtos.CategoryDTO;
import com.datn.endless.models.CategoryModel;
import com.datn.endless.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Tạo danh mục mới
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryModel categoryModel) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryModel);
        return ResponseEntity.ok(createdCategory);
    }

    // Lấy danh mục với filter, phân trang, tìm kiếm
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<CategoryDTO> categories = categoryService.getCategories(name, id, page, size);
        return ResponseEntity.ok(categories);
    }

    // Xóa danh mục
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable String id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Lấy danh mục theo ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable("id") String categoryId,
            @RequestBody CategoryModel categoryModel) {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, categoryModel);
        return ResponseEntity.ok(updatedCategory);
    }
}
