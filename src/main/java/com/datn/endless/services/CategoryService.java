package com.datn.endless.services;

import com.datn.endless.dtos.CategoryDTO;
import com.datn.endless.entities.Category;
import com.datn.endless.models.CategoryModel;
import com.datn.endless.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public CategoryDTO createCategory(CategoryModel categoryModel) {
        validateCategoryModel(categoryModel);

        // Kiểm tra xem tên danh mục đã tồn tại chưa
        if (categoryRepository.findByName(categoryModel.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại: " + categoryModel.getName());
        }

        Category newCategory = new Category();
        newCategory.setCategoryID(UUID.randomUUID().toString());
        newCategory.setName(categoryModel.getName());
        return convertToDTO(categoryRepository.save(newCategory));
    }

    @Transactional
    public CategoryDTO updateCategory(String id, CategoryModel categoryModel) {
        validateCategoryModel(categoryModel);

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục có ID: " + id));

        // Kiểm tra xem tên danh mục mới có trùng với tên đã tồn tại không
        if (!existingCategory.getName().equals(categoryModel.getName()) &&
                categoryRepository.findByName(categoryModel.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại: " + categoryModel.getName());
        }

        existingCategory.setName(categoryModel.getName());

        return convertToDTO(categoryRepository.save(existingCategory));
    }

    public Page<CategoryDTO> getCategoriesWithPaginationAndSearch(String keyword, Pageable pageable) {
        Page<Category> categories;
        if (keyword != null && !keyword.isEmpty()) {
            categories = categoryRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            categories = categoryRepository.findAll(pageable);
        }
        return categories.map(this::convertToDTO);
    }

    public Optional<CategoryDTO> getCategoryById(String id) {
        return categoryRepository.findById(id).map(this::convertToDTO);
    }

    @Transactional
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy danh mục có ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryID(category.getCategoryID());
        categoryDTO.setName(category.getName());
        // Thêm các thuộc tính khác nếu cần
        return categoryDTO;
    }

    private void validateCategoryModel(CategoryModel categoryModel) {
        if (!StringUtils.hasText(categoryModel.getName())) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }
    }
}
