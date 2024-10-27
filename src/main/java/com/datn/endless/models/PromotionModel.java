// PromotionModel.java
package com.datn.endless.models;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;

@Data
public class PromotionModel {
    private String promotionID;
    private String name;
//    private String enName;
    private LocalDate startDate;
    private LocalDate endDate;
    private MultipartFile poster;
//    private String enDescription;
}
