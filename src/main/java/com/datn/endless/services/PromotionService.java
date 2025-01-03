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
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionproductRepository promotionproductRepository;

    @Autowired
    private ProductversionRepository productversionRepository;

    public Page<PromotionDTO> getAllPromotions(String keyword, Instant startDate, Instant endDate, Pageable pageable) {
        // If keyword and start date, end date are provided
        if (keyword != null && !keyword.trim().isEmpty() && startDate != null && endDate != null) {
            return promotionRepository.findByNameContainingIgnoreCaseAndStartDateBetweenAndEndDateBetween(
                    keyword, startDate, endDate, pageable).map(this::convertToDTO);
        }

        // If only keyword is provided
        if (keyword != null && !keyword.trim().isEmpty()) {
            return promotionRepository.findByNameContainingIgnoreCase(keyword, pageable).map(this::convertToDTO);
        }

        // If only start date and end date are provided
        if (startDate != null && endDate != null) {
            return promotionRepository.findByStartDateBetweenAndEndDateBetween(startDate, endDate, pageable).map(this::convertToDTO);
        }

        // If neither keyword nor start date/end date are provided
        return promotionRepository.findAll(pageable).map(this::convertToDTO);
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
// Kiểm tra trùng tên khuyến mãi
        boolean isPromotionNameExist = promotionRepository.existsByName(promotionModel.getName());
        if (isPromotionNameExist) {
            throw new PromotionAlreadyExistsException("Tên khuyến mãi đã tồn tại");
        }
        // Tạo mới Promotion
        Promotion promotion = new Promotion();
        promotion.setPromotionID(UUID.randomUUID().toString());
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

        // Lấy danh sách các ProductVersionID hiện tại trong khuyến mãi
        Set<String> currentProductVersionIds = existingPromotion.getPromotionproducts().stream()
                .map(p -> p.getProductVersionID().getProductVersionID())
                .collect(Collectors.toSet());

        // Kiểm tra trùng lặp chỉ với các sản phẩm mới
        for (String productVersionId : promotionModel.getProductVersionIds()) {
            if (!currentProductVersionIds.contains(productVersionId)) {
                Productversion productVersion = productversionRepository.findById(productVersionId)
                        .orElseThrow(() -> new ProductVersionNotFoundException("Không tìm thấy phiên bản sản phẩm"));

                // Kiểm tra trùng khuyến mãi trong khoảng thời gian này
                boolean isOverlapping = promotionRepository.existsByProductVersionAndTimeOverlap(productVersion.getProductVersionID(), promotionModel.getStartDate(), promotionModel.getEndDate());
                if (isOverlapping) {
                    throw new PromotionAlreadyExistsException("Sản phẩm đã có khuyến mãi trong khoảng thời gian này");
                }
            }
        }
        // Kiểm tra trùng tên khuyến mãi
        boolean isPromotionNameExist = promotionRepository.existsByNameAndPromotionIDNot(promotionModel.getName(), promotionID);
        if (isPromotionNameExist) {
            throw new PromotionAlreadyExistsException("Tên khuyến mãi đã tồn tại");
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
                promotionproduct.setPromotionProductID(UUID.randomUUID().toString());
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
