package com.datn.endless.services;

import com.datn.endless.dtos.PromotionDTO;
import com.datn.endless.dtos.PromotionDetailDTO;
import com.datn.endless.entities.Promotion;
import com.datn.endless.entities.Promotiondetail;
import com.datn.endless.exceptions.PromotionNotFoundException;
import com.datn.endless.exceptions.PromotionAlreadyExistsException;
import com.datn.endless.exceptions.DuplicateDiscountException;
import com.datn.endless.exceptions.InvalidDiscountException;
import com.datn.endless.models.PromotionModel;
import com.datn.endless.repositories.PromotionRepository;
import com.datn.endless.repositories.PromotiondetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private PromotiondetailRepository promotiondetailRepository;

    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        PromotionModel promotionModel = toModel(promotionDTO);
        validatePromotionModel(promotionModel);

        // Chuyển đổi PromotionDTO sang Entity
        Promotion promotion = toEntity(promotionModel);

        // Lưu khuyến mãi vào cơ sở dữ liệu
        promotionRepository.save(promotion);

        // Lưu các chi tiết khuyến mãi
        savePromotionDetails(promotion, promotionDTO.getPromotionDetails());

        return toDTOWithDetails(promotion);
    }

    private void validatePromotionModel(PromotionModel promotionModel) {
        // Kiểm tra trùng tên chỉ nếu tên thay đổi
        if (promotionRepository.findByName(promotionModel.getName()).isPresent() &&
                !promotionRepository.findByName(promotionModel.getName()).get().getPromotionID().equals(promotionModel.getPromotionID())) {
            throw new PromotionAlreadyExistsException("Tên khuyến mãi đã tồn tại.");
        }

        // Kiểm tra trùng thời gian chỉ với các khuyến mãi khác, không phải chính nó
        List<Promotion> existingPromotions = promotionRepository.findAll();
        for (Promotion existingPromotion : existingPromotions) {
            if (!existingPromotion.getPromotionID().equals(promotionModel.getPromotionID()) && isOverlapping(existingPromotion, promotionModel)) {
                throw new IllegalArgumentException("Khoảng thời gian khuyến mãi không hợp lệ.");
            }
        }
    }

    private boolean isOverlapping(Promotion existingPromotion, PromotionModel newPromotion) {
        return !newPromotion.getStartDate().isAfter(existingPromotion.getEndDate()) &&
                !newPromotion.getEndDate().isBefore(existingPromotion.getStartDate());
    }

    @Transactional
    public PromotionDTO updatePromotion(String id, PromotionDTO promotionDTO) {
        // Tìm khuyến mãi cũ
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Khuyến mãi không tồn tại."));

        // Cập nhật khuyến mãi chính
        promotion.setName(promotionDTO.getName());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setPoster(promotionDTO.getPoster());

        // Nếu mảng promotionDetails là rỗng, xóa tất cả các chi tiết khuyến mãi cũ
        if (promotionDTO.getPromotionDetails().isEmpty()) {
            promotiondetailRepository.deleteByPromotionID(promotion);
        } else {
            // Cập nhật các chi tiết khuyến mãi mới
            savePromotionDetails(promotion, promotionDTO.getPromotionDetails());
        }

        // Lưu lại khuyến mãi
        promotionRepository.save(promotion);

        return toDTOWithDetails(promotion);
    }


    private void savePromotionDetails(Promotion promotion, List<PromotionDetailDTO> promotionDetailsDTO) {
        // Nếu promotionDetailsDTO là null, khởi tạo nó là danh sách rỗng
        if (promotionDetailsDTO == null) {
            promotionDetailsDTO = new ArrayList<>();
        }

        // Kiểm tra trùng lặp giảm giá trong promotionDetailsDTO
        Set<Integer> discountSet = new HashSet<>();
        for (PromotionDetailDTO detailDTO : promotionDetailsDTO) {
            if (!discountSet.add(detailDTO.getPercentDiscount())) {
                throw new DuplicateDiscountException("Giảm giá trùng lặp trong một khuyến mãi.");
            }

            // Kiểm tra giá trị percentDiscount có hợp lệ không (từ 0 đến 100)
            if (detailDTO.getPercentDiscount() < 0 || detailDTO.getPercentDiscount() > 100) {
                throw new InvalidDiscountException("Giảm giá phải nằm trong khoảng từ 0 đến 100.");
            }
        }

        // Lưu hoặc cập nhật PromotionDetails
        List<Promotiondetail> promotionDetails = promotionDetailsDTO.stream()
                .map(dto -> {
                    Promotiondetail detail = new Promotiondetail();
                    detail.setPromotionID(promotion);
                    detail.setPercentDiscount(dto.getPercentDiscount());
                    return detail;
                })
                .collect(Collectors.toList());

        // Lưu tất cả chi tiết khuyến mãi
        promotiondetailRepository.saveAll(promotionDetails);
    }



    public void deletePromotion(String id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new PromotionNotFoundException("Promotion không tồn tại với ID: " + id));

        // Xóa tất cả các chi tiết khuyến mãi liên quan
        promotiondetailRepository.deleteAll(promotion.getPromotionDetails());

        // Xóa khuyến mãi
        promotionRepository.deleteById(id);
    }

    public Page<PromotionDTO> findPromotionsByCriteria(String name, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // Lấy các khuyến mãi theo tiêu chí
        Page<Promotion> promotions = promotionRepository.findByCriteria(name, startDate, endDate, pageable);
        // Chuyển đổi thành PromotionDTO và bổ sung PromotionDetails
        return promotions.map(this::toDTOWithDetails);
    }

    private PromotionDTO toDTOWithDetails(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setPromotionID(promotion.getPromotionID());
        dto.setName(promotion.getName());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setPoster(promotion.getPoster());

        // Kiểm tra và khởi tạo danh sách promotionDetails nếu null
        List<PromotionDetailDTO> detailDTOs = Optional.ofNullable(promotion.getPromotionDetails())
                .orElse(new ArrayList<>()) // Nếu promotionDetails là null, tạo một danh sách rỗng
                .stream() // Chuyển sang stream
                .map(this::toPromotionDetailDTO) // Sử dụng phương thức toPromotionDetailDTO
                .collect(Collectors.toList());

        dto.setPromotionDetails(detailDTOs);

        return dto;
    }

    private PromotionDetailDTO toPromotionDetailDTO(Promotiondetail promotiondetail) {
        PromotionDetailDTO dto = new PromotionDetailDTO();
        dto.setPromotionDetailID(promotiondetail.getPromotionDetailID());
        dto.setPromotionID(promotiondetail.getPromotionID().getPromotionID()); // Lấy PromotionID từ liên kết
        dto.setPercentDiscount(promotiondetail.getPercentDiscount());
        return dto;
    }

    private PromotionModel toModel(PromotionDTO dto) {
        PromotionModel model = new PromotionModel();
        model.setName(dto.getName());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());
        model.setPoster(dto.getPoster());
        return model;
    }

    private Promotion toEntity(PromotionModel model) {
        Promotion promotion = new Promotion();
        promotion.setName(model.getName());
        promotion.setStartDate(model.getStartDate());
        promotion.setEndDate(model.getEndDate());
        promotion.setPoster(model.getPoster());
        return promotion;
    }
}
