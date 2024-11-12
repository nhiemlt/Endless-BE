package com.datn.endless.services;

import com.datn.endless.dtos.ProductVersionDTO;
import com.datn.endless.dtos.ProductVersionDTO1;
import com.datn.endless.dtos.PromotionproductDTO;
import com.datn.endless.entities.Promotiondetail;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Promotionproduct;
import com.datn.endless.models.PromotionProductModel;
import com.datn.endless.repositories.PromotionproductRepository;
import com.datn.endless.repositories.PromotiondetailRepository;
import com.datn.endless.repositories.ProductversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionProductService {

    @Autowired
    private PromotionproductRepository promotionProductRepository;

    @Autowired
    private PromotiondetailRepository promotionDetailRepository;

    @Autowired
    private ProductversionRepository productVersionRepository;


    public List<PromotionproductDTO> createPromotionProduct(String promotionDetailID, List<String> productVersionIDs) {
        Promotiondetail promotionDetail = promotionDetailRepository.findById(promotionDetailID)
                .orElseThrow(() -> new RuntimeException("PromotionDetail not found with ID: " + promotionDetailID));

        List<PromotionproductDTO> createdProducts = new ArrayList<>();

        for (String productVersionID : productVersionIDs) {
            Productversion productVersion = productVersionRepository.findById(productVersionID)
                    .orElseThrow(() -> new RuntimeException("ProductVersion not found with ID: " + productVersionID));

            Promotionproduct newProduct = new Promotionproduct();
            newProduct.setPromotionDetailID(promotionDetail);
            newProduct.setProductVersionID(productVersion);

            createdProducts.add(convertToDTO(promotionProductRepository.save(newProduct)));
        }

        return createdProducts;
    }


    public PromotionproductDTO updatePromotionProduct(String id, PromotionProductModel promotionProductModel) {
        Promotionproduct existingProduct = promotionProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PromotionProduct not found with ID: " + id));

        // Tìm PromotionDetail và ProductVersion từ cơ sở dữ liệu
        Promotiondetail promotionDetail = promotionDetailRepository.findById(promotionProductModel.getPromotionDetailID())
                .orElseThrow(() -> new RuntimeException("PromotionDetail not found with ID: " + promotionProductModel.getPromotionDetailID()));

        // Cập nhật PromotionDetail
        existingProduct.setPromotionDetailID(promotionDetail);

        // Xóa tất cả các mối quan hệ cũ
        List<Promotionproduct> oldRelations = promotionProductRepository.findByPromotionDetailID(promotionDetail);
        if (!oldRelations.isEmpty()) {
            // Xóa tất cả các mối quan hệ cũ
            promotionProductRepository.deleteAll(oldRelations);
        }

        // Nếu productVersionIDs không rỗng, thêm lại các mối quan hệ mới từ productVersionIDs
        if (!promotionProductModel.getProductVersionIDs().isEmpty()) {
            for (String productVersionID : promotionProductModel.getProductVersionIDs()) {
                Productversion productVersion = productVersionRepository.findById(productVersionID)
                        .orElseThrow(() -> new RuntimeException("ProductVersion not found with ID: " + productVersionID));

                Promotionproduct newProduct = new Promotionproduct();
                newProduct.setPromotionDetailID(promotionDetail);
                newProduct.setProductVersionID(productVersion);
                promotionProductRepository.save(newProduct);
            }
        }

        // Lưu lại sản phẩm đã cập nhật
        Promotionproduct updatedProduct = promotionProductRepository.save(existingProduct);

        return convertToDTO(updatedProduct);
    }



    public void deletePromotionProduct(String id) {
        if (!promotionProductRepository.existsById(id)) {
            throw new RuntimeException("Promotion not found with ID: " + id);
        }
        promotionProductRepository.deleteById(id);
    }

    // Phương thức để lấy danh sách có phân trang và lọc theo percentDiscount
    public Page<PromotionproductDTO> getAllPromotionProducts(Pageable pageable, Double percentDiscount) {
        // Lọc theo percentDiscount nếu có
        Page<Promotionproduct> entities;
        if (percentDiscount != null) {
            entities = promotionProductRepository.findByPromotionDetailPercentDiscount(pageable, percentDiscount);
        } else {
            entities = promotionProductRepository.findAll(pageable);
        }

        return entities.map(this::convertToDTO);
    }
    private PromotionproductDTO convertToDTO(Promotionproduct entity) {
        PromotionproductDTO dto = new PromotionproductDTO();
        dto.setPromotionProductID(entity.getPromotionProductID());
        dto.setPromotionDetailID(entity.getPromotionDetailID().getPromotionDetailID());
        dto.setPercentDiscount(entity.getPromotionDetailID().getPercentDiscount());

        // Lấy tất cả các ProductVersion liên quan đến PromotionDetail này
        List<ProductVersionDTO1> productVersionDTOs = promotionProductRepository
                .findByPromotionDetailID(entity.getPromotionDetailID())
                .stream()
                .map(va -> {
                    ProductVersionDTO1 productVersionDTO = new ProductVersionDTO1();
                    productVersionDTO.setProductVersionID(va.getProductVersionID().getProductVersionID());
                    productVersionDTO.setVersionName(va.getProductVersionID().getVersionName());
                    return productVersionDTO;
                })
                .collect(Collectors.toList());

        dto.setProductVersionIDs(productVersionDTOs); // Set danh sách ProductVersion vào DTO
        return dto;
    }


}
