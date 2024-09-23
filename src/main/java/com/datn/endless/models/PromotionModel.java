// PromotionModel.java
package com.datn.endless.models;


import lombok.Data;


import java.time.LocalDate;

@Data
public class PromotionModel {
    private String promotionID;
    private String name;
    private String enName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String poster;
    private String enDescription;
}
