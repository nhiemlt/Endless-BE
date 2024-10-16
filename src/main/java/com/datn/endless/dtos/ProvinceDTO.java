package com.datn.endless.dtos;

import lombok.Data;

@Data
public class ProvinceDTO {
    private String code;
    private String name;
    private String nameEn;
    private String fullName;
    private String fullNameEn;
    private String codeName;
    private Integer administrativeUnitId;
    private Integer administrativeRegionId;
}
