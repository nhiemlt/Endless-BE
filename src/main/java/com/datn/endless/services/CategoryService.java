package com.datn.endless.services;

import com.datn.endless.dtos.CategoryDTO;
import com.datn.endless.entities.Category;
import com.datn.endless.models.CategoryModel;
import com.datn.endless.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryDTO createCategory(CategoryModel categoryModel) {
        Category newCategory = new Category();
        newCategory.setCategoryID(UUID.randomUUID().toString());
        newCategory.setName(categoryModel.getName());
        newCategory.setEnName(categoryModel.getEnName());
        return convertToDTO(categoryRepository.save(newCategory));
    }

    public List<CategoryDTO> getCategories(String name, String enName, String id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage;

        // Nếu có ID, tìm kiếm theo ID
        if (id != null && !id.isEmpty()) {
            Optional<Category> categoryOpt = categoryRepository.findById(id);
            return categoryOpt.map(this::convertToDTO)
                    .map(List::of) // Trả về danh sách chứa CategoryDTO
                    .orElse(List.of()); // Trả về danh sách rỗng nếu không tìm thấy
        }

        // Tìm kiếm theo tên hoặc tên tiếng Anh
        if (name != null && !name.isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (enName != null && !enName.isEmpty()) {
            categoryPage = categoryRepository.findByEnNameContainingIgnoreCase(enName, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }

        // Chuyển đổi và trả về danh sách CategoryDTO
        return categoryPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();
    }



    public Optional<CategoryDTO> getCategoryById(String id) {
        return categoryRepository.findById(id).map(this::convertToDTO);
    }

    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryID(category.getCategoryID());
        categoryDTO.setName(category.getName());
        categoryDTO.setEnName(category.getEnName());
        return categoryDTO;
    }
}
