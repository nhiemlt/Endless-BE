package com.datn.endless.controllers;

import com.datn.endless.dtos.BrandDTO;
import com.datn.endless.dtos.CategoryDTO;
import com.datn.endless.dtos.ErrorResponse;
import com.datn.endless.models.CategoryModel;
import com.datn.endless.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Lấy danh mục với filter, phân trang, tìm kiếm
    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryDTO> categories = categoryService.getCategoriesWithPaginationAndSearch(keyword, pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable String id) {
        Optional<CategoryDTO> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get()); // Trả về CategoryDTO nếu tìm thấy
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Không tìm thấy danh mục", "Danh mục với ID " + id + " không tồn tại")); // Cập nhật ErrorResponse
        }
    }

    // Tạo danh mục mới
    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryModel categoryModel, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.datn.endless.utils.ErrorResponse("Đầu vào không hợp lệ", result.getAllErrors()));
        }
        try {
            CategoryDTO createdCategory = categoryService.createCategory(categoryModel);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new com.datn.endless.utils.ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable String id,
                                            @Valid @RequestBody CategoryModel categoryModel, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new com.datn.endless.utils.ErrorResponse("Đầu vào không hợp lệ", result.getAllErrors()));
        }
        try {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryModel);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new com.datn.endless.utils.ErrorResponse(e.getMessage()));
    }
    }

    // Xóa danh mục
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    private List<String> createErrorResponse(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
    }
}
