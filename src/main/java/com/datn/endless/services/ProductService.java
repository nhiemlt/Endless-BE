package com.datn.endless.services;

import com.datn.endless.dtos.BrandDTO;
import com.datn.endless.dtos.CategoryDTO;
import com.datn.endless.dtos.ProductDTO;
import com.datn.endless.entities.Brand;
import com.datn.endless.entities.Category;
import com.datn.endless.entities.Product;
import com.datn.endless.models.ProductModel;
import com.datn.endless.repositories.BrandRepository;
import com.datn.endless.repositories.CategoryRepository;
import com.datn.endless.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    // Tạo mới sản phẩm
    public ProductDTO createProduct(ProductModel productModel) {
        validateProductModel(productModel);

        // Kiểm tra trùng tên sản phẩm
        if (productRepository.findByName(productModel.getName()).isPresent()) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại: " + productModel.getName());
        }

        Product newProduct = new Product();
        newProduct.setProductID(UUID.randomUUID().toString());
        newProduct.setName(productModel.getName());
        newProduct.setDescription(productModel.getDescription());

        // Thiết lập danh mục và thương hiệu
        Category category = categoryRepository.findById(productModel.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục."));
        Brand brand = brandRepository.findById(productModel.getBrandID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu."));
        newProduct.setCategoryID(category);
        newProduct.setBrandID(brand);

        return convertToDTO(productRepository.save(newProduct));
    }

    // Cập nhật sản phẩm
    public ProductDTO updateProduct(String id, ProductModel productModel) {
        validateProductModel(productModel);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        // Kiểm tra trùng tên sản phẩm
        Optional<Product> productWithSameName = productRepository.findByName(productModel.getName());
        if (productWithSameName.isPresent() && !productWithSameName.get().getProductID().equals(id)) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại: " + productModel.getName());
        }

        existingProduct.setName(productModel.getName());
        existingProduct.setDescription(productModel.getDescription());

        Category category = categoryRepository.findById(productModel.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục."));
        Brand brand = brandRepository.findById(productModel.getBrandID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu."));
        existingProduct.setCategoryID(category);
        existingProduct.setBrandID(brand);

        return convertToDTO(productRepository.save(existingProduct));
    }

    public Page<ProductDTO> getProducts(String keyword, int page, int size, String categoryID, String brandID, String sortBy, String direction) {
        // Thiết lập tham số sắp xếp
        Sort sort = Sort.by(Sort.Order.by(sortBy));
        sort = direction.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(page, size, sort);  // Sử dụng Pageable với sort

        Page<Product> productPage;

        // Nếu có keyword, tìm kiếm theo keyword (bao gồm tên, danh mục và thương hiệu)
        if (StringUtils.hasText(keyword)) {
            productPage = productRepository.findByKeyword(keyword, pageable);
        }
        // Nếu có cả categoryID và brandID
        else if (categoryID != null && brandID != null) {
            productPage = productRepository.findByCategoryAndBrand(categoryID, brandID, pageable);
        }
        // Nếu lọc theo categoryID
        else if (categoryID != null) {
            productPage = productRepository.findByCategoryID(categoryID, pageable);
        }
        // Nếu lọc theo brandID
        else if (brandID != null) {
            productPage = productRepository.findByBrandID(brandID, pageable);
        }
        // Nếu không có keyword, categoryID hoặc brandID, lấy tất cả sản phẩm
        else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.map(this::convertToDTO); // Chuyển đổi sang DTO
    }

    // Lấy thông tin chi tiết của sản phẩm theo ID
    public Optional<ProductDTO> getProductById(String id) {
        return productRepository.findById(id)
                .map(this::convertToDTO)
                .or(() -> {
                    throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
                });
    }

    // Xóa sản phẩm theo ID
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductID(product.getProductID());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());

        // Chuyển đổi Category thành DTO nếu có
        if (product.getCategoryID() != null) {
            productDTO.setCategoryID(convertCategoryToDTO(product.getCategoryID()));
        }

        // Chuyển đổi Brand thành DTO nếu có
        if (product.getBrandID() != null) {
            productDTO.setBrandID(convertBrandToDTO(product.getBrandID()));
        }

        return productDTO;
    }

    // Phương thức chuyển đổi Category
    private CategoryDTO convertCategoryToDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryID(category.getCategoryID());
        categoryDTO.setName(category.getName());
        return categoryDTO;
    }

    // Phương thức chuyển đổi Brand
    private BrandDTO convertBrandToDTO(Brand brand) {
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setBrandID(brand.getBrandID());
        brandDTO.setBrandName(brand.getName());
        brandDTO.setLogo(brand.getLogo());
        return brandDTO;
    }


    // Kiểm tra dữ liệu đầu vào
    private void validateProductModel(ProductModel productModel) {
        if (!StringUtils.hasText(productModel.getName())) {
            throw new RuntimeException("Tên sản phẩm không được để trống.");
        }
        if (!StringUtils.hasText(productModel.getDescription())) {
            throw new RuntimeException("Mô tả sản phẩm không được để trống.");
        }
        if (productModel.getCategoryID() == null) {
            throw new RuntimeException("Danh mục không được để trống.");
        }
        if (productModel.getBrandID() == null) {
            throw new RuntimeException("Thương hiệu không được để trống.");
        }
    }
}
