package com.datn.endless.services;

import com.datn.endless.dtos.PromotionDetailDTO;
import com.datn.endless.entities.Promotion;
import com.datn.endless.entities.Promotiondetail;
import com.datn.endless.models.PromotionDetailModel;
import com.datn.endless.repositories.PromotiondetailRepository;
import com.datn.endless.repositories.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Promotion với ID: " + promotionDetailModel.getPromotionID()));

        newDetail.setPromotionID(promotion);
        newDetail.setPercentDiscount(promotionDetailModel.getPercentDiscount());

        return convertToDTO(promotionDetailRepository.save(newDetail));
    }

    public PromotionDetailDTO updatePromotionDetail(String id, PromotionDetailModel promotionDetailModel) {
        Promotiondetail existingDetail = promotionDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy PromotionDetail với ID: " + id));

        existingDetail.setPercentDiscount(promotionDetailModel.getPercentDiscount());

        // Tìm Promotion từ cơ sở dữ liệu
        Promotion promotion = promotionRepository.findById(promotionDetailModel.getPromotionID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Promotion với ID: " + promotionDetailModel.getPromotionID()));

        existingDetail.setPromotionID(promotion);

        return convertToDTO(promotionDetailRepository.save(existingDetail));
    }

    // Thêm phương thức phân trang vào PromotionDetailService
    public Page<PromotionDetailDTO> getAllPromotionDetails(Pageable pageable) {
        return promotionDetailRepository.findAll(pageable).map(this::convertToDTO);
    }

    public Optional<PromotionDetailDTO> getPromotionDetailById(String id) {
        return promotionDetailRepository.findById(id).map(this::convertToDTO);
    }

    public void deletePromotionDetail(String id) {
        if (!promotionDetailRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy PromotionDetail với ID: " + id);
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
