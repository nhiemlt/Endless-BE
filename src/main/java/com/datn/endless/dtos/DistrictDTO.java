package com.datn.endless.dtos;

import lombok.Data;

@Data
public class DistrictDTO {
    private String code;
    private String name;
    private String nameEn;
    private String fullName;
    private String fullNameEn;
    private String codeName;
    private String provinceCode;
    private Integer administrativeUnitId;
}
