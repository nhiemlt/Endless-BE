    package com.datn.endless.dtos;

    import lombok.Data;
    import java.time.LocalDate;
    @Data
        public class PromotionDTO {
            private String promotionID;
            private String name;
            private LocalDate startDate;
            private LocalDate endDate;
            private String poster;
        }

