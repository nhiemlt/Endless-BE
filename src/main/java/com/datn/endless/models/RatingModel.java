package com.datn.endless.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Data
public class RatingModel {
    @NotNull(message = "Order Detail ID cannot be null")
    private String orderDetailId;

    @Min(value = 1, message = "Rating value must be at least 1")
    @Max(value = 5, message = "Rating value must be at most 5")
    private Integer ratingValue;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;

    private  MultipartFile[] pictures;
}

