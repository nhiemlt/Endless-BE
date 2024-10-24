package com.datn.endless.dtos;

import lombok.Data;


import java.time.Instant;
import java.util.List;

@Data
public class RatingDTO2 {
    private String ratingID;
    private String userID;
    private String username;
    private String fullname;
    private String avatar;
    private String orderDetailID;
    private String productVersionID;
    private String versionName;
    private String image;
    private Integer ratingValue;
    private String comment;
    private Instant ratingDate;
    private List<RatingPictureDTO> pictures;
}
