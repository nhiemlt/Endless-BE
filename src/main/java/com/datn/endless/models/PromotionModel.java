package com.datn.endless.models;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.Instant;
import java.util.Set;

@Data
public class PromotionModel {

    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(max = 100, message = "Tên khuyến mãi không được vượt quá 100 ký tự")
    String name;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là hiện tại hoặc tương lai")
    Instant startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc phải là hiện tại hoặc tương lai")
    Instant endDate;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @Min(value = 1, message = "Phần trăm giảm giá phải lớn hơn hoặc bằng 0")
    @Max(value = 80, message = "Phần trăm giảm giá không được vượt quá 80")
    Integer percentDiscount;

    @NotBlank(message = "Poster không được để trống")
    String poster;

    @PastOrPresent(message = "Ngày tạo phải là hiện tại hoặc trước đó")
    Instant createDate;

    @NotEmpty(message = "Danh sách phiên bản sản phẩm không được để trống")
    Set<String> productVersionIds;
}

