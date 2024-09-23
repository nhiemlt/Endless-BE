
package com.datn.endless.services;

import com.datn.endless.dtos.PromotionDTO;
import com.datn.endless.entities.Promotion;
import com.datn.endless.repositories.PromotiondetailRepository;
import com.datn.endless.repositories.PromotionproductRepository;
import com.datn.endless.repositories.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;

    // Thêm phương thức tìm kiếm
    public Page<PromotionDTO> getPromotions(String name, LocalDate startDate, LocalDate endDate, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        return promotionRepository.findByCriteria(name, startDate, endDate, pageable).map(this::convertToPromotionDTO);
    }
    // Promotion functions
    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::convertToPromotionDTO)
                .collect(Collectors.toList());
    }

    public PromotionDTO createPromotion(PromotionDTO dto) {
        Promotion promotion = convertToPromotionEntity(dto);
        return convertToPromotionDTO(promotionRepository.save(promotion));
    }

    public PromotionDTO updatePromotion(String id, PromotionDTO dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        // Update properties
        promotion.setName(dto.getName());
        promotion.setEnName(dto.getEnName());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setPoster(dto.getPoster());
        promotion.setEnDescription(dto.getEnDescription());

        return convertToPromotionDTO(promotionRepository.save(promotion));
    }

    public void deletePromotion(String id) {
        promotionRepository.deleteById(id);
    }

    private PromotionDTO convertToPromotionDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setPromotionID(promotion.getPromotionID());
        dto.setName(promotion.getName());
        dto.setEnName(promotion.getEnName());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setPoster(promotion.getPoster());
        dto.setEnDescription(promotion.getEnDescription());
        return dto;
    }

    private Promotion convertToPromotionEntity(PromotionDTO dto) {
        Promotion promotion = new Promotion();
        promotion.setPromotionID(dto.getPromotionID());
        promotion.setName(dto.getName());
        promotion.setEnName(dto.getEnName());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setPoster(dto.getPoster());
        promotion.setEnDescription(dto.getEnDescription());
        return promotion;
    }


}
