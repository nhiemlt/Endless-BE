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

    // Tạo mới sản phẩm với kiểm tra lỗi
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

    // Cập nhật thông tin sản phẩm với kiểm tra lỗi
    public ProductDTO updateProduct(String id, ProductModel productModel) {
        validateProductModel(productModel);

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        // Kiểm tra trùng tên (ngoại trừ chính sản phẩm đang được cập nhật)
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

    public List<ProductDTO> getProducts(String name, String categoryId, String brandId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        // Nếu có tên sản phẩm, tìm kiếm theo tên
        if (StringUtils.hasText(name)) {
            productPage = productRepository.findByNameContainingIgnoreCase(name, pageable);
        }
        // Nếu không có tên sản phẩm nhưng có categoryId hoặc brandId
        else if (StringUtils.hasText(categoryId) || StringUtils.hasText(brandId)) {
            // Tìm kiếm theo danh mục hoặc thương hiệu
            productPage = productRepository.findByCategoryIDOrBrandID(categoryId, brandId, pageable);
        }
        // Nếu không có điều kiện nào, lấy tất cả sản phẩm
        else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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

    // Chuyển đổi từ Product entity sang ProductDTO
    private ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductID(product.getProductID());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());

        // Chuyển đổi Category thành DTO
        Optional.ofNullable(product.getCategoryID()).ifPresent(category -> {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryID(category.getCategoryID());
            categoryDTO.setName(category.getName());
            productDTO.setCategoryID(categoryDTO);
        });

        // Chuyển đổi Brand thành DTO
        Optional.ofNullable(product.getBrandID()).ifPresent(brand -> {
            BrandDTO brandDTO = new BrandDTO();
            brandDTO.setBrandID(brand.getBrandID());
            brandDTO.setBrandName(brand.getName());
            brandDTO.setLogo(brand.getLogo());
            productDTO.setBrandID(brandDTO);
        });

        return productDTO;
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
