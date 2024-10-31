    package com.datn.endless.dtos;

    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import lombok.Data;
    import java.time.LocalDate;
    @Data
        public class PromotionDTO {
            private String promotionID;

        @NotBlank(message = "Tên khuyến mãi không được để trống")
        private String name;

        @NotNull(message = "Ngày bắt đầu không được để trống")
        private LocalDate startDate;

        @NotNull(message = "Ngày kết thúc không được để trống")
        private LocalDate endDate;

        @NotBlank(message = "URL poster không được để trống")
        private String poster;
        }

