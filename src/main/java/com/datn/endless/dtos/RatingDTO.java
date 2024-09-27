package com.datn.endless.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Data
public class RatingDTO {
    private String ratingID;
    private String userID;
    private String orderDetailID;
    private Integer ratingValue;
    private String comment;
    private Instant ratingDate;
    private double averageRating;
    private List<RatingPictureDTO> pictures;
}
