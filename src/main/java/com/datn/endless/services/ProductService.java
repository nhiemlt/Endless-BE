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

    public ProductDTO createProduct(ProductModel productModel) {
        Product newProduct = new Product();
        newProduct.setProductID(UUID.randomUUID().toString());
        newProduct.setName(productModel.getName());
        newProduct.setDescription(productModel.getDescription());

        // Thiết lập CategoryID và BrandID từ ProductModel
        Category category = categoryRepository.findById(productModel.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Brand brand = brandRepository.findById(productModel.getBrandID())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        newProduct.setCategoryID(category);
        newProduct.setBrandID(brand);

        return convertToDTO(productRepository.save(newProduct));
    }

    public List<ProductDTO> getProducts(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;

        if (name != null && !name.isEmpty()) {
            productPage = productRepository.findByNameContainingIgnoreCase(name, pageable);
        }else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(String id) {
        return productRepository.findById(id).map(this::convertToDTO);
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }

    public ProductDTO updateProduct(String id, ProductModel productModel) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        existingProduct.setName(productModel.getName());
        existingProduct.setDescription(productModel.getDescription());

        Category category = categoryRepository.findById(productModel.getCategoryID())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Brand brand = brandRepository.findById(productModel.getBrandID())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        existingProduct.setCategoryID(category);
        existingProduct.setBrandID(brand);

        return convertToDTO(productRepository.save(existingProduct));
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductID(product.getProductID());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());

        // Chuyển đổi Category và Brand thành DTO
        if (product.getCategoryID() != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryID(product.getCategoryID().getCategoryID());
            categoryDTO.setName(product.getCategoryID().getName());
            productDTO.setCategoryID(categoryDTO);
        }

        if (product.getBrandID() != null) {
            BrandDTO brandDTO = new BrandDTO();
            brandDTO.setBrandID(product.getBrandID().getBrandID());
            brandDTO.setBrandName(product.getBrandID().getName());
            brandDTO.setLogo(product.getBrandID().getLogo());
            productDTO.setBrandID(brandDTO);
        }

        return productDTO;
    }
}
