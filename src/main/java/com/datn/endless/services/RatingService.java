package com.datn.endless.services;

import com.datn.endless.dtos.RatingDTO;
import com.datn.endless.dtos.RatingDTO2;
import com.datn.endless.dtos.RatingPictureDTO;
import com.datn.endless.entities.*;
import com.datn.endless.exceptions.DuplicateResourceException;
import com.datn.endless.exceptions.EntityNotFoundException;
import com.datn.endless.exceptions.ForbidenException;
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
import java.util.*;
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

    @Autowired
    private OrderstatusRepository orderstatusRepository;

    // Thêm đánh giá
    public RatingDTO2 addRating(RatingModel ratingModel) {
        // Kiểm tra người dùng hiện tại
        User user = userRepository.findByUsername(userLoginInfomation.getCurrentUsername());

        // Kiểm tra chi tiết đơn hàng
        Orderdetail orderDetail = orderDetailRepository.findById(ratingModel.getOrderDetailId())
                .orElseThrow(() -> new OrderNotFoundException("Chi tiết đơn hàng không tìm thấy"));

        // Kiểm tra quyền của user đối với chi tiết đơn hàng
        if (!orderDetail.getOrderID().getUserID().getUserID().equals(user.getUserID())) {
            throw new ForbidenException("Người dùng không có quyền đánh giá chi tiết đơn hàng này");
        }
        if (ratingRepository.existsByOrderDetailID_orderDetailID(orderDetail.getOrderDetailID())) {
            throw new DuplicateResourceException("Bạn đã đánh giá rồi");
        }
        if (!orderstatusRepository.findTopByOrderIdOrderByTimeDesc(orderDetail.getOrderID().getOrderID()).get().getStatusType().getName().equals("Đã giao hàng")){
            throw new DuplicateResourceException("Không thể đánh giá hóa đơn này");
        }

        // Tạo mới đối tượng Rating
        Rating rating = new Rating();
        rating.setRatingID(UUID.randomUUID().toString());
        rating.setUserID(user);
        rating.setOrderDetailID(orderDetail);
        rating.setRatingValue(ratingModel.getRatingValue());
        rating.setComment(ratingModel.getComment());
        rating.setRatingDate(Instant.now());
        Set<Ratingpicture> ratingpictureSet = new HashSet<>();
        for (String picture : ratingModel.getPictures()) {
            Ratingpicture ratingPicture = new Ratingpicture();
            ratingPicture.setPictureID(picture);
            ratingPicture.setRatingID(rating);
            ratingPicture.setPictureID(UUID.randomUUID().toString());
            ratingpictureSet.add(ratingPicture);
        }
        rating.setRatingpictures(ratingpictureSet);

        // Lưu đánh giá
        Rating savedRating = ratingRepository.save(rating);

        // Xử lý hình ảnh nếu có

        return convertToDTO2(savedRating);
    }

    // Chuyển đổi đối tượng Rating sang RatingDTO
    private RatingDTO2 convertToDTO2(Rating rating) {
        RatingDTO2 ratingDTO2 = new RatingDTO2();
        ratingDTO2.setRatingID(rating.getRatingID());
        ratingDTO2.setUserID(rating.getUserID().getUserID());
        ratingDTO2.setUsername(rating.getUserID().getUsername());
        ratingDTO2.setFullname(rating.getUserID().getFullname());
        ratingDTO2.setAvatar(rating.getUserID().getAvatar());
        ratingDTO2.setOrderDetailID(rating.getOrderDetailID().getOrderDetailID());
        ratingDTO2.setProductVersionID(rating.getOrderDetailID().getProductVersionID().getProductVersionID());
        ratingDTO2.setVersionName(rating.getOrderDetailID().getProductVersionID().getVersionName());
        ratingDTO2.setImage(rating.getOrderDetailID().getProductVersionID().getImage());
        ratingDTO2.setRatingValue(rating.getRatingValue());
        ratingDTO2.setComment(rating.getComment());
        ratingDTO2.setRatingDate(rating.getRatingDate());

        // Thêm hình ảnh vào DTO
        List<RatingPictureDTO> pictureDTOs = rating.getRatingpictures().stream()
                .map(this::convertToPictureDTO)
                .collect(Collectors.toList());

        ratingDTO2.setPictures(pictureDTOs);
        return ratingDTO2;
    }

    // Chuyển đổi đối tượng Rating sang RatingDTO
    private RatingDTO convertToDTO(Rating rating) {
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setRatingID(rating.getRatingID());
        ratingDTO.setUserID(rating.getUserID().getUserID());
        ratingDTO.setUsername(rating.getUserID().getUsername());
        ratingDTO.setFullname(rating.getUserID().getFullname());
        ratingDTO.setAvatar(rating.getUserID().getAvatar());
        ratingDTO.setOrderDetailID(rating.getOrderDetailID().getOrderDetailID());
        ratingDTO.setProductVersionID(rating.getOrderDetailID().getProductVersionID().getProductVersionID());
        ratingDTO.setVersionName(rating.getOrderDetailID().getProductVersionID().getVersionName());
        ratingDTO.setImage(rating.getOrderDetailID().getProductVersionID().getImage());
        ratingDTO.setRatingValue(rating.getRatingValue());
        ratingDTO.setComment(rating.getComment());
        ratingDTO.setRatingDate(rating.getRatingDate());

        // Thêm hình ảnh vào DTO
        List<RatingPictureDTO> pictureDTOs = rating.getRatingpictures().stream()
                .map(this::convertToPictureDTO)
                .collect(Collectors.toList());

        ratingDTO.setPictures(pictureDTOs);
        return ratingDTO;
    }

    public Page<RatingDTO2> getRatingsByKeyWord(String keyword, int ratingValue, int month, int year, Pageable pageable) {
        Page<Rating> ratings = ratingRepository.findByKeyWord(keyword, ratingValue, month, year, pageable);
        return ratings.map(this::convertToDTO2);
    }


    // Chuyển đổi đối tượng RatingPicture sang RatingPictureDTO
    private RatingPictureDTO convertToPictureDTO(Ratingpicture ratingPicture) {
        RatingPictureDTO dto = new RatingPictureDTO();
        dto.setPictureID(ratingPicture.getPictureID());
        dto.setRatingID(ratingPicture.getRatingID().getRatingID());
        dto.setPicture(ratingPicture.getPicture());
        return dto;
    }

    // Lấy đánh giá theo ID
    public RatingDTO2 getRatingById2(String id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Đánh giá không tìm thấy"));

        return convertToDTO2(rating);
    }

    public RatingDTO getRatingById(String id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Đánh giá không tìm thấy"));

        return convertToDTO(rating);
    }

    // Lấy đánh giá theo productVersionID và tính trung bình rating
    public List<RatingDTO> getRatingsByProductVersionId(String productVersionID) {
        List<Rating> ratings = ratingRepository.findByOrderDetailID_ProductVersionID_ProductVersionID(productVersionID);

        // Tính điểm trung bình rating
        OptionalDouble averageRating = ratings.stream()
                .mapToDouble(Rating::getRatingValue)
                .average();

        double average = averageRating.isPresent() ? averageRating.getAsDouble() : 0;

        // Chuyển đổi danh sách Rating sang RatingDTO và gán giá trị averageRating
        List<RatingDTO> ratingDTOs = ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        ratingDTOs.forEach(dto -> dto.setAverageRating(average));

        return ratingDTOs;
    }

    // Đếm số lượng đánh giá của phiên bản sản phẩm
    public long getRatingCountByProductVersionId(String productVersionID) {
        return ratingRepository.countByOrderDetailID_ProductVersionID_ProductVersionID(productVersionID);
    }

    // Xóa đánh giá theo ID
    public void deleteRatingById(String ratingId) {
        // Tìm kiếm đánh giá theo ID
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new EntityNotFoundException("Đánh giá không tìm thấy"));

        // Xóa tất cả hình ảnh liên quan đến đánh giá nếu có
        Set<Ratingpicture> ratingPictures = rating.getRatingpictures();
        ratingPictureRepository.deleteAll(ratingPictures);

        // Xóa đánh giá
        ratingRepository.delete(rating);
    }

    public Long getTotalRatingsCount() {
        return ratingRepository.countTotalRatings();
    }

    public Double calculateWeightedAverageRating() {
        List<Object[]> ratingsGrouped = ratingRepository.findRatingsGroupedByValue();
        double totalRatingValue = 0; // Tổng giá trị sao
        long totalCount = 0; // Tổng số lượt đánh giá

        for (Object[] row : ratingsGrouped) {
            Integer ratingValue = (Integer) row[0];  // Mức sao (1, 2, 3, 4, 5)
            Long count = (Long) row[1];              // Số lượt đánh giá cho mức sao đó

            totalRatingValue += ratingValue * count; // Cộng tổng điểm (ratingValue * count)
            totalCount += count;                     // Cộng tổng số lượt đánh giá
        }

        if (totalCount == 0) {
            return 0.0; // Trả về 0 nếu không có đánh giá nào
        }

        return totalRatingValue / totalCount; // Tính trung bình trọng số
    }

}