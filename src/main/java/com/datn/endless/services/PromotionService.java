package com.datn.endless.services;

import com.datn.endless.dtos.PromotionDTO;
import com.datn.endless.entities.Promotion;
import com.datn.endless.exceptions.ConvertImageException;
import com.datn.endless.models.PromotionModel;
import com.datn.endless.repositories.PromotionRepository;
import com.datn.endless.utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    public PromotionDTO createPromotion(PromotionModel promotionModel) {
        Promotion newPromotion = new Promotion();
        newPromotion.setPromotionID(UUID.randomUUID().toString());
        newPromotion.setName(promotionModel.getName());
        newPromotion.setEnName(promotionModel.getEnName());
        newPromotion.setStartDate(promotionModel.getStartDate());
        newPromotion.setEndDate(promotionModel.getEndDate());
        newPromotion.setEnDescription(promotionModel.getEnDescription());

        if (promotionModel.getPoster() != null && !promotionModel.getPoster().isEmpty()) {
            try {
                newPromotion.setPoster(ImageUtil.convertToBase64(promotionModel.getPoster()));
            } catch (IOException e) {
                throw new ConvertImageException("Could not convert poster to Base64");
            }
        }

        return convertToDTO(promotionRepository.save(newPromotion));
    }

    public PromotionDTO updatePromotion(String id, PromotionModel promotionModel) {
        Promotion existingPromotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with ID: " + id));

        existingPromotion.setName(promotionModel.getName());
        existingPromotion.setEnName(promotionModel.getEnName());
        existingPromotion.setStartDate(promotionModel.getStartDate());
        existingPromotion.setEndDate(promotionModel.getEndDate());
        existingPromotion.setEnDescription(promotionModel.getEnDescription());

        if (promotionModel.getPoster() != null && !promotionModel.getPoster().isEmpty()) {
            try {
                existingPromotion.setPoster(ImageUtil.convertToBase64(promotionModel.getPoster()));
            } catch (IOException e) {
                throw new ConvertImageException("Could not convert poster to Base64");
            }
        }

        return convertToDTO(promotionRepository.save(existingPromotion));
    }

    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<PromotionDTO> getPromotionById(String id) {
        return promotionRepository.findById(id).map(this::convertToDTO);
    }

    public void deletePromotion(String id) {
        if (!promotionRepository.existsById(id)) {
            throw new RuntimeException("Promotion not found with ID: " + id);
        }
        promotionRepository.deleteById(id);
    }

    private PromotionDTO convertToDTO(Promotion promotion) {
        PromotionDTO promotionDTO = new PromotionDTO();
        promotionDTO.setPromotionID(promotion.getPromotionID());
        promotionDTO.setName(promotion.getName());
        promotionDTO.setEnName(promotion.getEnName());
        promotionDTO.setStartDate(promotion.getStartDate());
        promotionDTO.setEndDate(promotion.getEndDate());
        promotionDTO.setPoster(promotion.getPoster());
        promotionDTO.setEnDescription(promotion.getEnDescription());
        return promotionDTO;
    }

    public Page<PromotionDTO> findPromotionsByCriteria(String name, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return promotionRepository.findByCriteria(name, startDate, endDate, pageable)
                .map(this::convertToDTO);
    }
}
