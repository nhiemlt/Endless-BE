package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private String reportID;
    private String title;
    private String description;
    private Date creationDate;
    private String createdBy;
    private Boolean isActive;
}
