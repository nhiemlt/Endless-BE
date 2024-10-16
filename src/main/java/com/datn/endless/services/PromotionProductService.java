package com.datn.endless.services;

import com.datn.endless.dtos.PromotionproductDTO;
import com.datn.endless.entities.Promotiondetail;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Promotionproduct;
import com.datn.endless.models.PromotionProductModel;
import com.datn.endless.repositories.PromotionproductRepository;
import com.datn.endless.repositories.PromotiondetailRepository;
import com.datn.endless.repositories.ProductversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionProductService {

    @Autowired
    private PromotionproductRepository promotionProductRepository;

    @Autowired
    private PromotiondetailRepository promotionDetailRepository;

    @Autowired
    private ProductversionRepository productVersionRepository;

    public PromotionproductDTO createPromotionProduct(PromotionProductModel promotionProductModel) {
        Promotionproduct newProduct = new Promotionproduct();

        // Tìm PromotionDetail và ProductVersion từ cơ sở dữ liệu
        Promotiondetail promotionDetail = promotionDetailRepository.findById(promotionProductModel.getPromotionDetailID())
                .orElseThrow(() -> new RuntimeException("PromotionDetail not found with ID: " + promotionProductModel.getPromotionDetailID()));

        Productversion productVersion = productVersionRepository.findById(promotionProductModel.getProductVersionID())
                .orElseThrow(() -> new RuntimeException("ProductVersion not found with ID: " + promotionProductModel.getProductVersionID()));

        newProduct.setPromotionDetailID(promotionDetail);
        newProduct.setProductVersionID(productVersion);

        return convertToDTO(promotionProductRepository.save(newProduct));
    }

    public PromotionproductDTO updatePromotionProduct(String id, PromotionProductModel promotionProductModel) {
        Promotionproduct existingProduct = promotionProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PromotionProduct not found with ID: " + id));

        // Tìm PromotionDetail và ProductVersion từ cơ sở dữ liệu
        Promotiondetail promotionDetail = promotionDetailRepository.findById(promotionProductModel.getPromotionDetailID())
                .orElseThrow(() -> new RuntimeException("PromotionDetail not found with ID: " + promotionProductModel.getPromotionDetailID()));

        Productversion productVersion = productVersionRepository.findById(promotionProductModel.getProductVersionID())
                .orElseThrow(() -> new RuntimeException("ProductVersion not found with ID: " + promotionProductModel.getProductVersionID()));

        // Cập nhật thông tin
        existingProduct.setPromotionDetailID(promotionDetail);
        existingProduct.setProductVersionID(productVersion);

        // Lưu lại sản phẩm đã cập nhật
        Promotionproduct updatedProduct = promotionProductRepository.save(existingProduct);

        return convertToDTO(updatedProduct);
    }


    public List<PromotionproductDTO> getAllPromotionProducts() {
        return promotionProductRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<PromotionproductDTO> getPromotionProductById(String id) {
        return promotionProductRepository.findById(id).map(this::convertToDTO);
    }

    public void deletePromotionProduct(String id) {
        if (!promotionProductRepository.existsById(id)) {
            throw new RuntimeException("Promotion not found with ID: " + id);
        }
        promotionProductRepository.deleteById(id);
    }


    private PromotionproductDTO convertToDTO(Promotionproduct product) {
        PromotionproductDTO dto = new PromotionproductDTO();
        dto.setPromotionProductID(product.getPromotionProductID());
        dto.setPromotionDetailID(product.getPromotionDetailID().getPromotionDetailID());
        dto.setProductVersionID(product.getProductVersionID().getProductVersionID());
        return dto;
    }
}
