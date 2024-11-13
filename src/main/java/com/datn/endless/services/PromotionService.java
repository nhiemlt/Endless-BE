package com.datn.endless.services;

import com.datn.endless.dtos.ProductversionPromotionDTO;
import com.datn.endless.dtos.PromotionDTO;
import com.datn.endless.dtos.PromotionproductDTO;
import com.datn.endless.entities.Productversion;
import com.datn.endless.entities.Promotion;
import com.datn.endless.entities.Promotionproduct;
import com.datn.endless.exceptions.ProductVersionNotFoundException;
import com.datn.endless.exceptions.PromotionAlreadyExistsException;
import com.datn.endless.exceptions.PromotionNotFoundException;
import com.datn.endless.repositories.ProductversionRepository;
import com.datn.endless.repositories.PromotionRepository;
import com.datn.endless.repositories.PromotionproductRepository;
import com.datn.endless.models.PromotionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionproductRepository promotionproductRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    // Lấy tất cả khuyến mãi với các tham số lọc khoảng thời gian, phân trang và sắp xếp
    public Page<PromotionDTO> getAllPromotions(String keyword, Pageable pageable) {
        // Nếu keyword không null, tìm kiếm theo tên
        if (keyword != null && !keyword.trim().isEmpty()) {
            return promotionRepository.findByNameContainingIgnoreCase(keyword, pageable)
                    .map(this::convertToDTO);
        }

        // Nếu không có keyword, trả về tất cả
        return promotionRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    // Lấy khuyến mãi theo ID
    public PromotionDTO getPromotionById(String promotionID) {
        Optional<Promotion> promotion = promotionRepository.findById(promotionID);
        return promotion.map(this::convertToDTO).orElse(null);
    }

    public PromotionDTO createPromotion(PromotionModel promotionModel) {
        // Kiểm tra xem sản phẩm đã có khuyến mãi trong khoảng thời gian này chưa
        for (String productVersionId : promotionModel.getProductVersionIds()) {
            Productversion productVersion = productversionRepository.findById(productVersionId)
                    .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));

            // Kiểm tra xem sản phẩm đã tham gia khuyến mãi nào chưa trong khoảng thời gian này
            boolean isOverlapping = promotionRepository.existsByProductVersionAndTimeOverlap(productVersion.getProductVersionID(), promotionModel.getStartDate(), promotionModel.getEndDate());
            if (isOverlapping) {
                throw new PromotionAlreadyExistsException("Sản phẩm đã có khuyến mãi trong khoảng thời gian này");
            }
        }

        // Tạo mới Promotion
        Promotion promotion = new Promotion();
        promotion.setName(promotionModel.getName());
        promotion.setStartDate(promotionModel.getStartDate());
        promotion.setEndDate(promotionModel.getEndDate());
        promotion.setPercentDiscount(promotionModel.getPercentDiscount());
        promotion.setPoster(promotionModel.getPoster());
        promotion.setActive(true);
        promotion.setCreateDate(Instant.now());

        Set<Promotionproduct> promotionproducts = new LinkedHashSet<>();
        for (String productVersionId : promotionModel.getProductVersionIds()) {
            Productversion productVersion = productversionRepository.findById(productVersionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản sản phẩm"));

            Promotionproduct promotionproduct = new Promotionproduct();
            promotionproduct.setPromotionID(promotion);
            promotionproduct.setProductVersionID(productVersion);

            promotionproducts.add(promotionproduct);
        }

        promotion.setPromotionproducts(promotionproducts);

        Promotion savedPromotion = promotionRepository.save(promotion);

        return convertToDTO(savedPromotion);
    }

    // Cập nhật khuyến mãi từ PromotionModel
    public PromotionDTO updatePromotion(String promotionID, PromotionModel promotionModel) {
        // Kiểm tra xem Promotion có tồn tại hay không
        Promotion existingPromotion = promotionRepository.findById(promotionID)
                .orElseThrow(() -> new PromotionNotFoundException("Không tìm thấy khuyến mãi"));

        // Kiểm tra xem sản phẩm có trùng khuyến mãi trong thời gian cập nhật không
        for (String productVersionId : promotionModel.getProductVersionIds()) {
            Productversion productVersion = productversionRepository.findById(productVersionId)
                    .orElseThrow(() -> new PromotionNotFoundException("Không tìm thấy phiên bản sản phẩm"));

            boolean isOverlapping = promotionRepository.existsByProductVersionAndTimeOverlap(productVersion.getProductVersionID(), promotionModel.getStartDate(), promotionModel.getEndDate());
            if (isOverlapping) {
                throw new PromotionAlreadyExistsException("Sản phẩm đã có khuyến mãi trong khoảng thời gian này");
            }
        }

        // Cập nhật thông tin khuyến mãi
        existingPromotion.setName(promotionModel.getName());
        existingPromotion.setStartDate(promotionModel.getStartDate());
        existingPromotion.setEndDate(promotionModel.getEndDate());
        existingPromotion.setPercentDiscount(promotionModel.getPercentDiscount());
        existingPromotion.setPoster(promotionModel.getPoster());
        existingPromotion.setCreateDate(Instant.now());

        // Xử lý Promotionproduct
        Set<String> newProductVersionIds = promotionModel.getProductVersionIds();
        Set<Promotionproduct> promotionproductsToRemove = new LinkedHashSet<>();
        for (Promotionproduct promotionproduct : existingPromotion.getPromotionproducts()) {
            if (!newProductVersionIds.contains(promotionproduct.getProductVersionID().getProductVersionID())) {
                promotionproductsToRemove.add(promotionproduct);
            }
        }
        existingPromotion.getPromotionproducts().removeAll(promotionproductsToRemove);
        promotionproductRepository.deleteAll(promotionproductsToRemove);

        Set<Promotionproduct> promotionproducts = existingPromotion.getPromotionproducts();
        for (String productVersionId : newProductVersionIds) {
            Productversion productVersion = productversionRepository.findById(productVersionId)
                    .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));

            boolean alreadyExists = promotionproducts.stream()
                    .anyMatch(p -> p.getProductVersionID().getProductVersionID().equals(productVersionId));

            if (!alreadyExists) {
                Promotionproduct promotionproduct = new Promotionproduct();
                promotionproduct.setPromotionID(existingPromotion);
                promotionproduct.setProductVersionID(productVersion);
                promotionproducts.add(promotionproduct);
            }
        }

        Promotion updatedPromotion = promotionRepository.save(existingPromotion);
        return convertToDTO(updatedPromotion);
    }

    public PromotionDTO toggleActive(String promotionID) {
        Promotion promotion = promotionRepository.findById(promotionID)
                .orElseThrow(() -> new PromotionNotFoundException("Không tìm thấy khuyến mãi"));
        promotion.setActive(!promotion.getActive());  // Nếu đang true thì thành false, và ngược lại
        Promotion updatedPromotion = promotionRepository.save(promotion);

        return convertToDTO(updatedPromotion);
    }

    // Xóa khuyến mãi
    public void deletePromotion(String promotionID) {
        Promotion promotion = promotionRepository.findById(promotionID)
                .orElseThrow(() -> new PromotionNotFoundException("Không tìm thấy khuyến mãi"));
            promotionRepository.delete(promotion);
    }

    private PromotionDTO convertToDTO(Promotion promotion) {
        return new PromotionDTO(
                promotion.getPromotionID(),
                promotion.getName(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.getPercentDiscount(),
                promotion.getPoster(),
                promotion.getActive(),
                promotion.getCreateDate(),
                promotion.getPromotionproducts()
                        .stream()
                        .map(this::convertToPromotionProductDTO)
                        .collect(Collectors.toSet())
        );
    }

    // Chuyển đổi Promotionproduct sang PromotionproductDTO
    private PromotionproductDTO convertToPromotionProductDTO(Promotionproduct promotionproduct) {
        return new PromotionproductDTO(
                promotionproduct.getPromotionProductID(),
                new ProductversionPromotionDTO(
                        promotionproduct.getProductVersionID().getProductVersionID(),
                        promotionproduct.getProductVersionID().getProductID().getName(),
                        promotionproduct.getProductVersionID().getVersionName(),
                        promotionproduct.getProductVersionID().getPurchasePrice(),
                        promotionproduct.getProductVersionID().getPrice(),
                        promotionproduct.getProductVersionID().getStatus(),
                        promotionproduct.getProductVersionID().getImage()
                )
        );
    }
}
