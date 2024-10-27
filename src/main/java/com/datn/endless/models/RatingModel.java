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
    @NotNull(message = "Id chi tiết hóa đơn không được trống")
    private String orderDetailId;

    @Min(value = 1, message = "Đánh giá phải lớn hơn 1")
    @Max(value = 5, message = "Đánh giá không vượt quá 5")
    private Integer ratingValue;

    @Size(max = 500, message = "Bình luận không vượt quá 500 kí tự")
    private String comment;

    private List<String> pictures;
}

