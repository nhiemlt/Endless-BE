package com.datn.endless.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UseraddressDTO {

    private String addressID;
    private String userID;
    private String provinceCode;
    private String districtCode;
    private String wardCode;
    private String houseNumberStreet;

}
