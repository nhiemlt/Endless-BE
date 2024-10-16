package com.datn.endless.services;

import com.datn.endless.dtos.PromotionDetailDTO;
import com.datn.endless.entities.Promotion;
import com.datn.endless.entities.Promotiondetail;
import com.datn.endless.models.PromotionDetailModel;
import com.datn.endless.repositories.PromotiondetailRepository;
import com.datn.endless.repositories.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionDetailService {

    @Autowired
    private PromotiondetailRepository promotionDetailRepository;

    @Autowired
    private PromotionRepository promotionRepository; // Inject PromotionRepository

    public PromotionDetailDTO createPromotionDetail(PromotionDetailModel promotionDetailModel) {
        Promotiondetail newDetail = new Promotiondetail();

        // Tìm Promotion từ cơ sở dữ liệu
        Promotion promotion = promotionRepository.findById(promotionDetailModel.getPromotionID())
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionDetailModel.getPromotionID()));

        newDetail.setPromotionID(promotion);
        newDetail.setPercentDiscount(promotionDetailModel.getPercentDiscount());

        return convertToDTO(promotionDetailRepository.save(newDetail));
    }

    public PromotionDetailDTO updatePromotionDetail(String id, PromotionDetailModel promotionDetailModel) {
        Promotiondetail existingDetail = promotionDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PromotionDetail not found with ID: " + id));

        existingDetail.setPercentDiscount(promotionDetailModel.getPercentDiscount());

        // Tìm Promotion từ cơ sở dữ liệu
        Promotion promotion = promotionRepository.findById(promotionDetailModel.getPromotionID())
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + promotionDetailModel.getPromotionID()));

        existingDetail.setPromotionID(promotion);

        return convertToDTO(promotionDetailRepository.save(existingDetail));
    }

    public List<PromotionDetailDTO> getAllPromotionDetails() {
        return promotionDetailRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<PromotionDetailDTO> getPromotionDetailById(String id) {
        return promotionDetailRepository.findById(id).map(this::convertToDTO);
    }

    public void deletePromotionDetail(String id) {
        if (!promotionDetailRepository.existsById(id)) {
            throw new RuntimeException("PromotionDetail not found with ID: " + id);
        }
        promotionDetailRepository.deleteById(id);
    }

    private PromotionDetailDTO convertToDTO(Promotiondetail detail) {
        PromotionDetailDTO dto = new PromotionDetailDTO();
        dto.setPromotionDetailID(detail.getPromotionDetailID());
        dto.setPromotionID(detail.getPromotionID().getPromotionID()); // Lấy ID của Promotion
        dto.setPercentDiscount(detail.getPercentDiscount());
        return dto;
    }
}
