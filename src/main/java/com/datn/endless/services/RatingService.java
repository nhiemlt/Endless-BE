package com.datn.endless.services;

import com.datn.endless.dtos.RatingDTO;
import com.datn.endless.dtos.RatingPictureDTO;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.EntityNotFoundException;
import com.datn.endless.exceptions.OrderNotFoundException;
import com.datn.endless.models.RatingModel;
import com.datn.endless.repositories.*;
import com.datn.endless.utils.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingpictureRepository ratingPictureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderdetailRepository orderDetailRepository;

    @Autowired
    private UserLoginInfomation userLoginInfomation;

    public RatingDTO addRating(RatingModel ratingModel) {
        // Kiểm tra người dùng
        User user = userRepository.findByUsername(userLoginInfomation.getCurrentUsername());

        // Kiểm tra chi tiết đơn hàng
        Orderdetail orderDetail = orderDetailRepository.findById(ratingModel.getOrderDetailId())
                .orElseThrow(() -> new OrderNotFoundException("Order detail not found"));

        // Kiểm tra xem orderDetail có thuộc về user không
        if (!orderDetail.getOrderID().getUserID().getUserID().equals(user.getUserID())) {
            throw new RuntimeException("User does not have permission to rate this order detail");
        }

        // Tạo mới đối tượng Rating
        Rating rating = new Rating();
        rating.setRatingID(UUID.randomUUID().toString());
        rating.setUserID(user);
        rating.setOrderDetailID(orderDetail);
        rating.setRatingValue(ratingModel.getRatingValue());
        rating.setComment(ratingModel.getComment());
        rating.setRatingDate(Instant.now());

        // Lưu Rating
        Rating savedRating = ratingRepository.save(rating);

        // Xử lý hình ảnh nếu có
        if (ratingModel.getPictures() != null) {
            for (MultipartFile file : ratingModel.getPictures()) {
                if (!file.isEmpty()) {
                    try {
                        Ratingpicture ratingPicture = new Ratingpicture();
                        ratingPicture.setPictureID(UUID.randomUUID().toString());
                        ratingPicture.setRatingID(savedRating);
                        ratingPicture.setPicture(ImageUtil.convertToBase64(file)); // Chuyển đổi hình ảnh

                        ratingPictureRepository.save(ratingPicture);
                    } catch (IOException e) {
                        throw new RuntimeException("Error processing image: " + file.getOriginalFilename(), e);
                    }
                }
            }
        }

        return convertToDTO(savedRating);
    }


    // Chuyển đổi đối tượng Rating sang RatingDTO
    private RatingDTO convertToDTO(Rating rating) {
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setRatingID(rating.getRatingID());
        ratingDTO.setUserID(rating.getUserID().getUserID());
        ratingDTO.setOrderDetailID(rating.getOrderDetailID().getOrderDetailID());
        ratingDTO.setRatingValue(rating.getRatingValue());
        ratingDTO.setComment(rating.getComment());
        ratingDTO.setRatingDate(rating.getRatingDate());

        // Thêm hình ảnh vào DTO
        List<RatingPictureDTO> pictureDTOs = rating.getRatingpictures().stream()
                .map(rp -> {
                    RatingPictureDTO dto = new RatingPictureDTO();
                    dto.setPictureID(rp.getPictureID());
                    dto.setRatingID(rp.getRatingID().getRatingID());
                    dto.setPicture(rp.getPicture());
                    return dto;
                })
                .collect(Collectors.toList());

        ratingDTO.setPictures(pictureDTOs);
        return ratingDTO;
    }

    // Lấy tất cả các đánh giá với lọc và phân trang
    public Page<RatingDTO> getAllRatings(String userId, Pageable pageable) {
        Page<Rating> ratings;

        if (userId != null) {
            ratings = ratingRepository.findByUserID_UserID(userId, pageable);
        } else {
            ratings = ratingRepository.findAll(pageable);
        }

        return ratings.map(this::convertToDTO);
    }

    // Lấy đánh giá theo ID
    public RatingDTO getRatingById(String id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rating not found"));

        return convertToDTO(rating);
    }

    // Lấy đánh giá theo productVersionID và tính trung bình rating
    public List<RatingDTO> getRatingsByProductVersionId(String productVersionID) {
        List<Rating> ratings = ratingRepository.findByOrderDetailID_ProductVersionID_ProductVersionID(productVersionID);

        // Tính tổng trung bình rating
        OptionalDouble averageRating = ratings.stream()
                .mapToDouble(Rating::getRatingValue)
                .average();

        double average = averageRating.isPresent() ? averageRating.getAsDouble() : 0;

        // Chuyển đổi các đối tượng Rating sang RatingDTO và đặt giá trị averageRating
        List<RatingDTO> ratingDTOs = ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        ratingDTOs.forEach(dto -> dto.setAverageRating(average));

        return ratingDTOs;
    }

    // Hàm tính số lượng đánh giá của phiên bản sản phẩm
    public long getRatingCountByProductVersionId(String productVersionID) {
        return ratingRepository.countByOrderDetailID_ProductVersionID_ProductVersionID(productVersionID);
    }
}
