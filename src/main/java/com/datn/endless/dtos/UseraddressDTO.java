package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UseraddressDTO {
    private String addressID;
    private String userID;
    private String provinceCode;
    private String districtCode;
    private String wardCode;
    private String houseNumberStreet;
    private String provinceName;
    private String districtName;
    private String wardName;
}
