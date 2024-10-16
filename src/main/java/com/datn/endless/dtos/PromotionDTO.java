    package com.datn.endless.dtos;

    import lombok.Data;
    import java.time.LocalDate;
    @Data
        public class PromotionDTO {
            private String promotionID;
            private String name;
            private String enName;
            private LocalDate startDate;
            private LocalDate endDate;
            private String poster;
            private String enDescription;
        }

