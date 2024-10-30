package com.datn.endless.services;

import com.datn.endless.dtos.PromotionDTO;
import com.datn.endless.entities.Promotion;
import com.datn.endless.models.PromotionModel;
import com.datn.endless.repositories.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    public PromotionDTO createPromotion(PromotionModel promotionModel) {
        validatePromotionModel(promotionModel);
        Promotion promotion = new Promotion(); // Giả sử bạn có một lớp Promotion entity
        // Thiết lập các trường cho Promotion từ promotionModel
        promotion.setName(promotionModel.getName());
        promotion.setStartDate(promotionModel.getStartDate());
        promotion.setEndDate(promotionModel.getEndDate());
        promotion.setPoster(promotionModel.getPoster());

        // Lưu khuyến mãi vào cơ sở dữ liệu
        promotionRepository.save(promotion);

        return convertToDTO(promotion); // Hàm chuyển đổi từ Promotion sang PromotionDTO
    }

    public PromotionDTO updatePromotion(String id, PromotionModel promotionModel) {
        validatePromotionModel(promotionModel);
        // Tìm kiếm khuyến mãi theo ID và cập nhật
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khuyến mãi không tồn tại."));

        // Cập nhật thông tin khuyến mãi
        promotion.setName(promotionModel.getName());
        promotion.setStartDate(promotionModel.getStartDate());
        promotion.setEndDate(promotionModel.getEndDate());
        promotion.setPoster(promotionModel.getPoster());

        // Lưu khuyến mãi đã cập nhật vào cơ sở dữ liệu
        promotionRepository.save(promotion);

        return convertToDTO(promotion);
    }

    private void validatePromotionModel(PromotionModel promotionModel) {
        if (!StringUtils.hasText(promotionModel.getName())) {
            throw new IllegalArgumentException("Tên khuyến mãi không được để trống.");
        }
        if (!StringUtils.hasText(promotionModel.getPoster())) {
            throw new IllegalArgumentException("Poster khuyến mãi không được để trống.");
        }

        // Kiểm tra trùng tên
        if (promotionRepository.findByName(promotionModel.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên khuyến mãi đã tồn tại: " + promotionModel.getName());
        }

        // Kiểm tra khoảng ngày
        List<Promotion> existingPromotions = promotionRepository.findAll();
        for (Promotion existingPromotion : existingPromotions) {
            if (isOverlapping(existingPromotion, promotionModel)) {
                throw new IllegalArgumentException("Khoảng thời gian khuyến mãi mới không được nằm trong khoảng thời gian của khuyến mãi khác.");
            }
        }
    }

    private boolean isOverlapping(Promotion existingPromotion, PromotionModel newPromotion) {
        return !newPromotion.getStartDate().isAfter(existingPromotion.getEndDate()) &&
                !newPromotion.getEndDate().isBefore(existingPromotion.getStartDate());
    }

    private PromotionDTO convertToDTO(Promotion promotion) {
        // Chuyển đổi từ Promotion entity sang PromotionDTO
        PromotionDTO promotionDTO = new PromotionDTO();
        promotionDTO.setPromotionID(promotion.getPromotionID());
        promotionDTO.setName(promotion.getName());
        promotionDTO.setStartDate(promotion.getStartDate());
        promotionDTO.setEndDate(promotion.getEndDate());
        promotionDTO.setPoster(promotion.getPoster());
        return promotionDTO;
    }
    public void deletePromotion(String id) {
        if (!promotionRepository.existsById(id)) {
            throw new RuntimeException("Promotion không tồn tại với ID: " + id);
        }
        promotionRepository.deleteById(id);
    }


    public Optional<PromotionDTO> getPromotionById(String id) {
        return promotionRepository.findById(id).map(this::convertToDTO);
    }



    public Page<PromotionDTO> findPromotionsByCriteria(String name, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return promotionRepository.findByCriteria(name, startDate, endDate, pageable)
                .map(this::convertToDTO);
    }
}
